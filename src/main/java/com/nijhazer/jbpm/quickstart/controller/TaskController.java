package com.nijhazer.jbpm.quickstart.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nijhazer.jbpm.quickstart.TaskResourceFactory;

@Controller
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    
    @Autowired
    private TaskResourceFactory taskFactory;
	
    @RequestMapping(value="/task/user/{username}",method = RequestMethod.GET)
    public String getAdminHomePage(@PathVariable("username") String username, Model model) {
        logger.info("Rendering Home Page");
        model.addAttribute("page_title", "yeah");
        return "home";
	 }

}