package beans;

public class Certification {
    private char acronym;
    private char name;
    private java.sql.Date certificationDate;
    private java.sql.Date expirationDate;

	public char getAcronym() {
		return acronym;
	}
	public void setAcronym(char acronym) {
		this.acronym = acronym;
	}
	public char getName() {
		return name;
	}
	public void setName(char name) {
		this.name = name;
	}
	public java.sql.Date getCertificationDate() {
		return certificationDate;
	}
	public void setCertificationDate(java.sql.Date certificationDate) {
		this.certificationDate = certificationDate;
	}
	public java.sql.Date getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(java.sql.Date expirationDate) {
		this.expirationDate = expirationDate;
	}

    
    
}
