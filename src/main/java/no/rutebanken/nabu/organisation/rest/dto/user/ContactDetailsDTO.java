package no.rutebanken.nabu.organisation.rest.dto.user;

public class ContactDetailsDTO {


	public String firstName;

	public String lastName;

	public String phone;

	public String email;

	public ContactDetailsDTO(String firstName, String lastName, String phone, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.email = email;
	}

	public ContactDetailsDTO() {
	}
}
