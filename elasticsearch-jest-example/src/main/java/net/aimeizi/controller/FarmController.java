package net.aimeizi.controller;

import net.aimeizi.model.Farm;
import net.aimeizi.service.FarmService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
public class FarmController {

    @Autowired
//	@Resource
    private FarmService farmService;

    @RequestMapping(value = "/farm", method = RequestMethod.GET)
    public String toFarmSearch() {
        return "farmSearch";
    }

    @RequestMapping(value = "/farm/search", method = RequestMethod.POST)
    public ModelAndView search(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("farmSearch");
        String field = request.getParameter("field");
        String queryString = request.getParameter("queryString");
        String older = request.getParameter("older");
        String pageNumber = request.getParameter("pageNumber");
        System.out.println(pageNumber);
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isEmpty(queryString)) {
            return modelAndView;
        }
        try {
            if (StringUtils.isEmpty(pageNumber) || StringUtils.isEmpty(pageSize)) {
                pageNumber = String.valueOf("1");
                pageSize = String.valueOf("10");
            }
            Map<String, Object> maps = farmService.search(field, queryString, older, Integer.parseInt(pageNumber), Integer.parseInt(pageSize));
            modelAndView.addObject("queryString", queryString);
            modelAndView.addObject("farms", (List<Farm>) maps.get("farms"));
            long count = (Long) maps.get("count");
            modelAndView.addObject("count", count);
            modelAndView.addObject("took", (Long) maps.get("took"));
            modelAndView.addObject("field", field);
            modelAndView.addObject("older", older);
            modelAndView.addObject("pageNumber", pageNumber);
            modelAndView.addObject("pageSize", pageSize);
            long cn = count % Integer.parseInt(pageSize) == 0 ? count / Integer.parseInt(pageSize) : count / Integer.parseInt(pageSize) + 1;
            if(cn>9){
                modelAndView.addObject("pages",2);
            }else {
                modelAndView.addObject("pages",(int)cn);
            }
            modelAndView.addObject("totalPages", count % Integer.parseInt(pageSize) == 0 ? count / Integer.parseInt(pageSize) : count / Integer.parseInt(pageSize) + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modelAndView;
    }

}