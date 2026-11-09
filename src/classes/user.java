package src.classes;

public abstract class user {
    private final String userId;
    private String name;
    private String contactInfo;

    public user(String userId, String name, String contactInfo) {
        this.userId = userId;
        this.name = name;
        this.contactInfo = contactInfo;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getContactInfo() { return contactInfo; }

    public void setName(String name) { this.name = name; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public abstract String getRole();

    public void displayUserInfo() {
        System.out.println("----------------------------------------");
        System.out.println("User ID: " + userId);
        System.out.println("Name: " + name);
        System.out.println("Role: " + getRole());
        System.out.println("Contact: " + contactInfo);
    }

public static class Student extends user {
    public Student(String id, String name, String contact) {
        super(id, name, contact);
    }
    @Override public String getRole() { return "Student"; }
}

public static class Teacher extends user {
    public Teacher(String id, String name, String contact) {
        super(id, name, contact);
    }
    @Override public String getRole() { return "Teacher"; }
}

public static class Staff extends user {
    public Staff(String id, String name, String contact) {
        super(id, name, contact);
    }
    @Override public String getRole() { return "Staff"; }
}

public static class Librarian extends user {
    public Librarian(String id, String name, String contact) {
        super(id, name, contact);
    }
    @Override public String getRole() { return "Librarian"; }
}
}


