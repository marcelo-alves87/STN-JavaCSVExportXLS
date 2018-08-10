package br.ufpe.utils;

public class ExportImportFile implements Comparable<ExportImportFile> {

	private String name;
	private String path;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExportImportFile other = (ExportImportFile) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int compareTo(ExportImportFile other) {
		if (other == null)
			return -1;
		if (name == null) {
			if (other.name != null)
				return 1;
		} else if (other.name == null)
			return -1;
		else if (name.length() < other.name.length())
			return -1;
		else if (name.length() > other.name.length())
			return 1;
		else if (name.compareTo(other.name) <= 0)
			return -1;
		else
			return 1;
		return 0;
	}

}
