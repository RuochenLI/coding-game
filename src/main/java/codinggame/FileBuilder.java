package builder;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * In some contests you will have to submit all your code in a single file. This
 * class is here to help you build this unique file by scanning the base class
 * of your code, reading the imported classes, parse them and build your file
 * containing all your imported classes as private classes in a unique file in
 * the default package
 * 
 * Usage:
 * 	Run the main of this class and pass as argument the path of the file where you have your main.
 * 
 * Example path : /src/builder/sample/Sample.java
 * 
 * @author Manwe
 * 
 */
public class FileBuilder {
	private static final String END_COMMENT = "*/";

	private static class ClassCode {
		private final String classFile;

		private String className;
		private String keyword;

		private final List<String> beforeClassContent = new ArrayList<>();
		private final List<String> afterClassContent = new ArrayList<>();

		ClassCode(String classFile) {
			this.classFile = classFile;
		}

		public void declaration(String line, String keyword) {
			className = extractDeclaration(line, keyword);
			this.keyword = keyword;
		}

		public String className() {
			return className;
		}

		public String declaration() {
			return keyword + className;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final ClassCode other = (ClassCode) obj;
			if (classFile == null) {
				if (other.classFile != null) {
					return false;
				}
			} else if (!classFile.equals(other.classFile)) {
				return false;
			}
			return true;
		}

		private String extractDeclaration(String line, String str) {
			return line.substring(line.indexOf(str) + str.length()).replaceAll("\\{", "").trim();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((classFile == null) ? 0 : classFile.hashCode());
			return result;
		}
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Unexpected number of arguments");
		} else {
			final FileBuilder builder = new FileBuilder();
			final ClassCode treated = builder.processFile(args[0]);
			builder.write(treated);
		}
	}

	private static final Charset CHARSET = Charset.forName("UTF-8");
	private final Set<String> imports = new HashSet<>();

	private final Set<String> knownFiles = new HashSet<>();

	private final Map<String, ClassCode> innerClasses = new HashMap<>();
	
	private FileBuilder() {
	}

	private String importToPath(String importStr) {
		final String className = importStr.substring(7).replaceAll(";", "");

		return toAbsolutePath("src/" + className.replaceAll("\\.", "/") + ".java");
	}

	private ClassCode processFile(String fileName) {
		System.out.println("reading class content of " + fileName);
		knownFiles.add(toAbsolutePath(fileName));
		final List<String> fileContent = readFile(fileName);
		final ClassCode code = readFileContent(fileName, fileContent);
		readPackageClasses(fileName);
		return code;
	}

	private List<String> readFile(String fileName) {
		try {
			return Files.readAllLines(Paths.get(fileName), CHARSET);
		} catch (final IOException e) {
			System.err.println("Error while reading file " + fileName);
			throw new IllegalStateException("Unable to continue");
		}
	}

	private ClassCode readFileContent(String fileName, List<String> fileContent) {
		final ClassCode code = new ClassCode(fileName);
		boolean fileKeyWordRead = false;
		boolean insideComment = false;
		for (final String line : fileContent) {
			String trimedLine = line.trim();
			if (insideComment) {
				if (trimedLine.contains(END_COMMENT)) {
					insideComment = false;
					String remainingCode = trimedLine.substring(trimedLine.indexOf(END_COMMENT)+END_COMMENT.length());
					if (!remainingCode.trim().isEmpty()){
						fileKeyWordRead = addLineToCode(code, fileKeyWordRead, remainingCode);	
					}
				}
				// We can skip comments since generated file size might be
				// limited
			} else if (trimedLine.isEmpty()) {
				// We don't need empty lines
			} else if (trimedLine.startsWith("//")) {
				// We can skip comments since generated file size might be
				// limited
			} else if (trimedLine.startsWith("/*")) {
				// We can skip comments since generated file size might be
				// limited
				if (!trimedLine.contains(END_COMMENT)){
					insideComment = true;	
				}
			} else {
				fileKeyWordRead = addLineToCode(code, fileKeyWordRead, line);
			}
		}
		return code;
	}

	private boolean addLineToCode(final ClassCode code, boolean fileKeyWordRead, final String line) {
		if (line.startsWith("package ")) {
			// Do nothing, we'll remove the package info
		} else if (line.startsWith("import ")) {
			final String importedClassPath = importToPath(line);
			if (!knownFiles.contains(importedClassPath)) {
				if (Files.exists(Paths.get(toAbsolutePath(importedClassPath)))){
					innerClasses.put(importedClassPath, processFile(importedClassPath));	
				}
				else{
					System.out.println("Standard import:"+line);
					imports.add(line);
				}
			}
		} else {
			if (fileKeyWordRead) {
				code.afterClassContent.add(line);
			} else {
				if (line.contains("class ")) {
					code.declaration(line, "class ");
					fileKeyWordRead = true;
				} else if (line.contains("interface ")) {
					code.declaration(line, "interface ");
					fileKeyWordRead = true;
				} else if (line.contains("enum ")) {
					code.declaration(line, "enum ");
					fileKeyWordRead = true;
				} else {
					code.beforeClassContent.add(line);
				}
			}
		}
		return fileKeyWordRead;
	}

	private void readPackageClasses(String fileName) {
		final Path directory = Paths.get(fileName).getParent();
		DirectoryStream<Path> ds;
		try {
			ds = Files.newDirectoryStream(directory);
			for (final Path child : ds) {
				final String absolutePath = toAbsolutePath(child);
				if (!Files.isDirectory(child) && absolutePath.endsWith(".java") && !knownFiles.contains(absolutePath)) {
					innerClasses.put(absolutePath, processFile(absolutePath));
				}
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private String toAbsolutePath(Path path) {
		return path.toFile().getAbsolutePath();
	}

	private String toAbsolutePath(String fileName) {
		return toAbsolutePath(Paths.get(fileName));
	}

	private void write(ClassCode treated) {
		final String outputFile = "src/" + treated.className() + ".java";

		final List<String> lines = new ArrayList<>();
		lines.addAll(imports);
		for (final String line : treated.beforeClassContent) {
			lines.add(line);
		}
		lines.add("class " + treated.className() + " {");
		for (final ClassCode innerClass : innerClasses.values()) {
			for (final String line : innerClass.beforeClassContent) {
				lines.add("\t" + line);
			}
			lines.add("\tprivate static " + innerClass.declaration() + " {");
			for (final String line : innerClass.afterClassContent) {
				lines.add("\t" + line);
			}
		}
		for (final String line : treated.afterClassContent) {
			lines.add(line);
		}

		try {
			Files.write(Paths.get(outputFile), lines, CHARSET);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
