package gamal.myappnew.instagram.Model;

public class Comment {
    String comment,publiser;

    public Comment(String comment, String publiser) {
        this.comment = comment;
        this.publiser = publiser;
    }

    public Comment() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPubliser() {
        return publiser;
    }

    public void setPubliser(String publiser) {
        this.publiser = publiser;
    }
}
