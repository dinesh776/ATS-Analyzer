package com.resume.analyzer.Controller;

import com.resume.analyzer.Services.AIChatClient;
import com.resume.analyzer.Model.Settings;
import com.resume.analyzer.Services.RepoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WebController {

    @Autowired
    private RepoService repoService;

    @GetMapping("/")
    public String home(Model model){
        Settings settings=repoService.getSettings();
        model.addAttribute("settings",settings==null?new Settings():settings);
        return "index";
    }

    @PostMapping("/settings")
    public String settings(@ModelAttribute Settings settings, RedirectAttributes attributes) {
        try {
            if (settings.getSamplingRate() == null) {
                settings.setSamplingRate(0.7);
            }
            String test = AIChatClient.getInstance(settings).test();

            if (test.equals("Success")) {
                repoService.save(settings);
                attributes.addFlashAttribute("successMessage", "Settings saved successfully");
            } else {
                attributes.addFlashAttribute("errorMessage", test);
            }
        } catch (Exception e) {
            attributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/";
    }


    @ModelAttribute("requestUri")
    public String requestUri(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();

        // Construct the base URL
        String baseUrl = scheme + "://" + serverName;

        // Add port if it's not the default port
        if ((scheme.equals("http") && serverPort != 80) ||
                (scheme.equals("https") && serverPort != 443)) {
            baseUrl += ":" + serverPort;
        }

        // Add context path if present
        baseUrl += contextPath;
        return baseUrl;
    }
}
