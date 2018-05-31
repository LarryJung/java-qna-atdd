package codesquad.web;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

public class QuestionAcceptanceTest extends AcceptanceTest{
    
    private final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    private HttpEntity<MultiValueMap<String, Object>> request;
    private ResponseEntity<String> response;

    @Test
    public void can_read_for_guest_user() {

    }

    @Test
    public void can_read_for_login_user() {

    }

    @Test
    public void can_create_for_login_user() {

    }

    @Test
    public void cannot_create_for_guest_user() {

    }

    @Test
    public void can_update_for_owner() {

    }

    @Test
    public void cannot_update_for_other_user() {

    }

    @Test
    public void cannot_update_for_guest_user() {

    }

    @Test
    public void can_delete_for_owner() {

    }

    @Test
    public void cannot_delete_for_other_user() {

    }

    @Test
    public void cannot_delete_for_guest_user() {

    }

}
