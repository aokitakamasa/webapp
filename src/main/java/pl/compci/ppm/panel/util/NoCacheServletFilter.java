package pl.compci.ppm.panel.util;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import java.io.IOException;

/**

 */
public class NoCacheServletFilter implements Filter {

  @Context
  private HttpServletRequest servletRequest;


  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
  {

    HttpServletRequest req = (HttpServletRequest) request;

    String url = req.getServletPath();
    try {
      String name = req.getUserPrincipal().getName();
      if(!name.isEmpty() && url.equals("/login.html")){
       ((HttpServletResponse) response).sendRedirect("/panel");
      }
    }catch (NullPointerException e){
      ;
    }

    String  value = "no-cache";
    ((HttpServletResponse)response).setHeader("Cache-Control", value);
    chain.doFilter(request, response);
  }

  public void destroy() {
  }
}
