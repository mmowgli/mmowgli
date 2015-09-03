package edu.nps.moves.mmowgli.test;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

@SuppressWarnings("serial")

@WebServlet(value = "/pushtest/*", asyncSupported = true)
@VaadinServletConfiguration(productionMode = false,ui = PushTestUI.class)

public class PushTestServlet extends VaadinServlet
{

}
