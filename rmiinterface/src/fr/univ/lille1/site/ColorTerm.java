package fr.univ.lille1.site;

/**
 * Enum that is used in order to define different color in each node (used for log)
 *
 * @author Durigneux Antoine
 * @author Dupont Cle½ment
 */
public enum ColorTerm {

	JAUNE("\033[33m"), MAUVE("\033[35m"), CYAN("\033[36m"), ROUGE("\033[31m"), VERT(
			"\033[32m"), BLEU("\033[34m");

	private String name = "";

	ColorTerm(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

}
