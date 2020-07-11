package gamal.myappnew.instagram.Model;

public class Post {

    String postid,postimage,descripition,publisher;

    public Post() {
    }

    public Post(String postid, String postimage, String descripition, String publisher) {
        this.postid = postid;
        this.postimage = postimage;
        this.descripition = descripition;
        this.publisher = publisher;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getDescripition() {
        return descripition;
    }

    public void setDescripition(String descripition) {
        this.descripition = descripition;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
