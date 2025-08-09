package com.empresa.contabilidade.alvara_monitor.controller;

import com.empresa.contabilidade.alvara_monitor.entities.Company;
import com.empresa.contabilidade.alvara_monitor.exceptions.BusinessException;
import com.empresa.contabilidade.alvara_monitor.exceptions.ResourceNotFoundException;
import com.empresa.contabilidade.alvara_monitor.repositories.UserRepository;
import com.empresa.contabilidade.alvara_monitor.services.CompanyService;
import com.empresa.contabilidade.alvara_monitor.services.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(CompanyControllerTest.TestConfig.class)
@WebMvcTest(CompanyController.class)
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CompanyService companyService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CompanyService companyService() {
            return Mockito.mock(CompanyService.class);
        }

        @Bean
        public TokenService tokenService() {
            return Mockito.mock(TokenService.class);
        }

        @Bean
        public UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
        }
    }

    @Nested
    @DisplayName("Testes para GET /api/companies")
    class ListCompaniesTests {

        @Test
        @DisplayName("Deve retornar status 200 OK e uma lista de empresas")
        @WithMockUser(roles = "USER")
        void shouldReturnOkAndListOfCompanies() throws Exception {
            var company1 = new Company(1L, "Empresa A", null, null, null, null);
            var company2 = new Company(2L, "Empresa B", null, null, null, null);
            when(companyService.listCompanies(null, null)).thenReturn(List.of(company1, company2));

            mockMvc.perform(get("/api/companies"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name", is("Empresa A")))
                    .andExpect(jsonPath("$[1].id", is(2)));
        }
    }

    @Nested
    @DisplayName("Testes para GET /api/companies/{id}")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar status 200 OK e a empresa quando o ID existe")
        @WithMockUser(roles = "USER")
        void shouldReturnOkAndCompanyWhenIdExists() throws Exception {
            var company = new Company(1L, "Empresa Encontrada", null, null, null, null);
            when(companyService.findById(1L)).thenReturn(company);

            mockMvc.perform(get("/api/companies/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Empresa Encontrada")));
        }

        @Test
        @DisplayName("Deve retornar status 404 Not Found quando o ID não existe")
        @WithMockUser(roles = "USER")
        void shouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
            when(companyService.findById(99L)).thenThrow(new ResourceNotFoundException("Empresa não encontrada"));

            mockMvc.perform(get("/api/companies/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Testes para POST /api/companies")
    class CreateCompanyTests {

        @Test
        @DisplayName("Deve retornar status 201 Created ao cadastrar empresa com dados válidos")
        @WithMockUser(roles = "USER")
        void shouldReturnCreatedWhenRegisteringCompany() throws Exception {
            final var inputData  = new Company();
            inputData .setName("Nova Empresa Teste");
            inputData .setExpLicenseFiredept(LocalDate.of(2025, 10, 1));

            final var savedCompany = new Company();
            savedCompany.setId(1L);

            when(companyService.save(any(Company.class))).thenReturn(savedCompany);

            mockMvc.perform(post("/api/companies")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputData )))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L));
        }

        @Test
        @DisplayName("Deve retornar status 400 Bad Request ao tentar cadastrar empresa com dados inválidos")
        @WithMockUser(roles = "USER")
        void shouldReturnBadRequestForInvalidData() throws Exception {
            final var invalidData = new Company();
            invalidData.setName("");
            invalidData.setExpLicenseFiredept(LocalDate.now());

            when(companyService.save(any(Company.class)))
                    .thenThrow(new BusinessException("O nome da empresa não pode ser vazio."));

            mockMvc.perform(post("/api/companies")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidData)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("O nome da empresa não pode ser vazio."));
        }
    }

    @Nested
    @DisplayName("Testes para PUT /api/companies/{id}")
    class UpdateCompanyTests {

        @Test
        @DisplayName("Deve retornar status 200 OK e a empresa atualizada")
        @WithMockUser(roles = "USER")
        void shouldReturnOkAndTheUpdatedCompany() throws Exception {
            var updatedData = new Company();
            updatedData.setName("Nome Atualizado");
            updatedData.setExpLicenseFiredept(LocalDate.now());

            var serviceResponse = new Company(1L, "Nome Atualizado", LocalDate.now(), null, null, null);

            when(companyService.update(eq(1L), any(Company.class))).thenReturn(serviceResponse);

            mockMvc.perform(put("/api/companies/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedData)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("Nome Atualizado")));
        }
    }

    @Nested
    @DisplayName("Testes para DELETE /api/companies/{id}")
    class DeleteCompanyTests {

        @Test
        @DisplayName("Deve retornar status 204 No Content quando a exclusão é bem-sucedida")
        @WithMockUser(roles = "USER")
        void shouldReturnNoContentOnSuccessfulDelete() throws Exception {
            mockMvc.perform(delete("/api/companies/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve retornar status 404 Not Found ao tentar deletar um ID que não existe")
        @WithMockUser(roles = "USER")
        void shouldReturnNotFoundWhenDeletingNonExistentId() throws Exception {
            doThrow(new ResourceNotFoundException("Empresa não encontrada")).when(companyService).delete(eq(99L));

            mockMvc.perform(delete("/api/companies/99"))
                    .andExpect(status().isNotFound());
        }
    }
}
