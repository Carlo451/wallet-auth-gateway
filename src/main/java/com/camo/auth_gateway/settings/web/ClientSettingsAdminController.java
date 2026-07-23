package com.camo.auth_gateway.settings.web;


import com.camo.auth_gateway.settings.domain.AskedClaimForRegister;
import com.camo.auth_gateway.settings.domain.ClientFlowType;
import com.camo.auth_gateway.settings.domain.ClientSettings;
import com.camo.auth_gateway.settings.dto.AskedClaimForRegisterForm;
import com.camo.auth_gateway.settings.repository.AskedClaimForRegisterRepository;
import com.camo.auth_gateway.settings.repository.ClientSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Objects;

@Controller
@RequestMapping("/admin/clients")
@RequiredArgsConstructor
public class ClientSettingsAdminController {

    private final ClientSettingRepository clientSettingsRepository;
    private final AskedClaimForRegisterRepository askedClaimForRegisterRepository;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("clientForm", new ClientSettings());
        model.addAttribute("askedClaimForm", new AskedClaimForRegisterForm());
        model.addAttribute("clients", clientSettingsRepository.findAll(Sort.by(Sort.Direction.ASC, "name")));
        //model.addAttribute("flowTypes", ClientFlowType.values());
        return "admin/clients";
    }

    @PostMapping
    public String createClient(@ModelAttribute("clientForm") ClientSettings clientForm) {

        clientSettingsRepository.save(clientForm);
        return "redirect:/admin/clients";
    }

    @PostMapping("/{id}/delete")
    public String deleteClient(@PathVariable Long id) {
        clientSettingsRepository.deleteById(id);
        return "redirect:/admin/clients";
    }

    @PostMapping("/{id}/asked-claims")
    public String addAskedClaim(@PathVariable Long id,
                                @ModelAttribute AskedClaimForRegisterForm askedClaimForm) {

        ClientSettings clientSettings = clientSettingsRepository.findById(id).orElseThrow();

        String claimName = askedClaimForm.getClaimName() == null ? "" : askedClaimForm.getClaimName().trim();
        if (!claimName.isBlank()) {
            AskedClaimForRegister claim = new AskedClaimForRegister();
            claim.setClaimName(claimName);
            clientSettings.addAskedClaimForRegister(claim);
            clientSettingsRepository.save(clientSettings);
        }

        return "redirect:/admin/clients";
    }

    @PostMapping("/{clientId}/asked-claims/{claimId}/delete")
    public String deleteAskedClaim(@PathVariable Long clientId,
                                   @PathVariable Long claimId) {

        ClientSettings clientSettings = clientSettingsRepository.findById(clientId).orElseThrow();
        AskedClaimForRegister claim = askedClaimForRegisterRepository.findById(claimId).orElseThrow();

        clientSettings.removeAskedClaimForRegister(claim);
        clientSettingsRepository.save(clientSettings);

        return "redirect:/admin/clients";
    }


}