package vit.sweater.sweater.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("t")
@TestPropertySource("/application-test.properties")
@Sql(value = {"/create.sql", "/mes.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/mesafter.sql", "/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MainController mainController;

    @Test
    public void mainPageTest() throws Exception {
        this.mockMvc.perform(get("/main"))
                .andDo(print())
                .andExpect(authenticated());
    }

    @Test
    public void usernameDisplaysCorrectTest() throws Exception{

        this.mockMvc.perform(get("/main"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id='navbarSupportedContent']/div").string("t"));
    }

    @Test
    public void messageListTest() throws Exception{

        this.mockMvc.perform(get("/main"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id='message-list']/div").nodeCount(3));

    }

    @Test
    public void filterTest() throws Exception{

        this.mockMvc.perform(get("/main").param("filter", "first tag"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id='message-list']/div").nodeCount(1))
                .andExpect(xpath("//div[@id='message-list']/div[@data-id=5]").exists());
     }

     @Test
    public void addMessageToListTest() throws Exception{
         MockHttpServletRequestBuilder multipart = multipart("/main")
                 .file("file", "123".getBytes())
                 .param("text", "FOURTH")
                 .param("tag", "FOUR")
                 .with(csrf());

         this.mockMvc.perform(multipart)
                 .andDo(print())
                 .andExpect(authenticated())
                 .andExpect(xpath("//div[@id='message-list']/div").nodeCount(4))
                 .andExpect(xpath("//div[@id='message-list']/div[@data-id=10]").exists())
                 .andExpect(xpath("//div[@id='message-list']/div[@data-id=10]/div/span").string("FOURTH"))
                 .andExpect(xpath("//div[@id='message-list']/div[@data-id=10]/div/i").string("#FOUR"));
     }
}