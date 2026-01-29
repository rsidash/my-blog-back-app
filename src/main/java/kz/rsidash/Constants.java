package kz.rsidash;

public interface Constants {
    String API_ROOT = "/api";

    // HEALTH
    String HEALTH_CHECK = API_ROOT + "/health";

    // POSTS
    String POSTS_ROOT = API_ROOT + "/posts";
    String POST = POSTS_ROOT + "/{postId}";

    // LIKES
    String POST_ADD_LIKE = POST + "/likes";

    // IMAGES
    String POST_IMAGE = POST + "/image";

    // COMMENTS
    String POST_COMMENTS = POST + "/comments";
    String POST_COMMENT = POST_COMMENTS + "/{commentId}";

    // FILES
    String FILES_ROOT = API_ROOT + "/files";
    String FILES_UPLOAD = FILES_ROOT + "/upload";
    String FILES_DOWNLOAD = FILES_ROOT + "/download/{filename}";

    interface Numbers {
        long LONG_ZERO = 0L;
        int INT_ZERO = 0;
        int INT_ONE = 1;
    }

}