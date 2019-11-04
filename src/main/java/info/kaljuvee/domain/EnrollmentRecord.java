package info.kaljuvee.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Represents an enrollment record.
 *
 * Note: The comparison and equals methods, along with the hash code method are specific to the requirements of the
 * parser, so make sure to review before using it as a general purpose domain object.
 *
 * @author Oliver Kaljuvee
 */
public class EnrollmentRecord implements Comparable<EnrollmentRecord> {
    private String userId;
    private int version;
    private String fullName;
    private String firstName;
    private String lastName;
    private String insuranceCompany;

    public EnrollmentRecord(String userId, int version, String fullName, String insuranceCompany) {
        this.userId = userId;
        this.version = version;
        this.fullName = fullName;
        this.insuranceCompany = insuranceCompany;

        int i = fullName.trim().lastIndexOf(" ");
        setFirstName(fullName.substring(0, i).trim());
        setLastName(fullName.substring(i).trim());
    }

    public String getUserId() {
        return userId;
    }

    public int getVersion() {
        return version;
    }

    public String getFullName() {
        return fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    /**
     * Represents a line item for the record that would be written to an output file.
     *
     * @return List of strings to be written to the output file.
     */
    public List getRecordLine() {
        List<String> lineItem = new LinkedList<>();
        lineItem.add(getUserId());
        lineItem.add(getFullName());
        lineItem.add(Integer.toString(getVersion()));
        lineItem.add(getInsuranceCompany());
        return lineItem;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EnrollmentRecord.class.getSimpleName() + "[", "]")
                .add("userId='" + userId + "'")
                .add("version=" + version)
                .add("fullName='" + fullName + "'")
                .add("firstName='" + firstName + "'")
                .add("lastName='" + lastName + "'")
                .add("insuranceCompany='" + insuranceCompany + "'")
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EnrollmentRecord)) return false;
        EnrollmentRecord that = (EnrollmentRecord) o;
        return getUserId().equals(that.getUserId()) &&
                getVersion() == that.getVersion() &&
                getInsuranceCompany().equals(that.getInsuranceCompany());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getVersion(), getInsuranceCompany());
    }

    /**
     * Comparable method for records, comparing by last name, then by first name, and finally by largest version. In
     * order to achieve different ordering behavior, consider using Comparators.
     *
     * @param o Other record
     * @return Comparator return values per specification
     */
    @Override
    public int compareTo(EnrollmentRecord o) {
        int compare = this.getLastName().compareTo(o.getLastName());

        if(compare == 0) {
            compare = this.getFirstName().compareTo(o.getFirstName());
        }
        // Important: Natural ordering by version means the latest version, per requirements in the parser
        if(compare == 0) {
            compare = o.getVersion() - this.getVersion();
        }
        return compare;
    }
}
