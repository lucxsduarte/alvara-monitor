package com.empresa.contabilidade.alvara_monitor.controller;

import com.empresa.contabilidade.alvara_monitor.exception.BusinessException;
import com.empresa.contabilidade.alvara_monitor.exception.ResourceNotFoundException;
import com.empresa.contabilidade.alvara_monitor.model.Empresa;
import com.empresa.contabilidade.alvara_monitor.repository.UsuarioRepository;
import com.empresa.contabilidade.alvara_monitor.service.EmpresaService;
import com.empresa.contabilidade.alvara_monitor.service.TokenService;
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

@Import(EmpresaControllerTest.TestConfig.class)
@WebMvcTest(EmpresaController.class)
class EmpresaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmpresaService empresaService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public EmpresaService empresaService() {
            return Mockito.mock(EmpresaService.class);
        }

        @Bean
        public TokenService tokenService() {
            return Mockito.mock(TokenService.class);
        }

        @Bean
        public UsuarioRepository usuarioRepository() {
            return Mockito.mock(UsuarioRepository.class);
        }
    }

    @Nested
    @DisplayName("Testes para GET /api/empresas")
    class ListarEmpresasTestes {

        @Test
        @DisplayName("Deve retornar status 200 OK e uma lista de empresas")
        @WithMockUser(roles = "USER")
        void deveRetornarOkEListaDeEmpresas() throws Exception {
            var empresa1 = new Empresa(1L, "Empresa A", null, null, null, null);
            var empresa2 = new Empresa(2L, "Empresa B", null, null, null, null);
            when(empresaService.listar(null, null)).thenReturn(List.of(empresa1, empresa2));

            mockMvc.perform(get("/api/empresas"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].nome", is("Empresa A")))
                    .andExpect(jsonPath("$[1].id", is(2)));
        }
    }

    @Nested
    @DisplayName("Testes para GET /api/empresas/{id}")
    class BuscarPorIdTestes {

        @Test
        @DisplayName("Deve retornar status 200 OK e a empresa quando o ID existe")
        @WithMockUser(roles = "USER")
        void deveRetornarOkEaEmpresaQuandoIdExiste() throws Exception {
            var empresa = new Empresa(1L, "Empresa Encontrada", null, null, null, null);
            when(empresaService.buscarPorId(1L)).thenReturn(empresa);

            mockMvc.perform(get("/api/empresas/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.nome", is("Empresa Encontrada")));
        }

        @Test
        @DisplayName("Deve retornar status 404 Not Found quando o ID não existe")
        @WithMockUser(roles = "USER")
        void deveRetornarNotFoundQuandoIdNaoExiste() throws Exception {
            when(empresaService.buscarPorId(99L)).thenThrow(new ResourceNotFoundException("Empresa não encontrada"));

            mockMvc.perform(get("/api/empresas/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Testes para POST /api/empresas")
    class CadastrarEmpresaTestes {

        @Test
        @DisplayName("Deve retornar status 201 Created ao cadastrar empresa com dados válidos")
        @WithMockUser(roles = "USER")
        void deveRetornarCreatedAoCadastrarEmpresa() throws Exception {
            final var dadosDeEntrada = new Empresa();
            dadosDeEntrada.setNome("Nova Empresa Teste");
            dadosDeEntrada.setVencBombeiros(LocalDate.of(2025, 10, 1));

            final var empresaSalva = new Empresa();
            empresaSalva.setId(1L);

            when(empresaService.salvar(any(Empresa.class))).thenReturn(empresaSalva);

            mockMvc.perform(post("/api/empresas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dadosDeEntrada)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L));
        }

        @Test
        @DisplayName("Deve retornar status 400 Bad Request ao tentar cadastrar empresa com dados inválidos")
        @WithMockUser(roles = "USER")
        void deveRetornarBadRequestAoTentarCadastrarComDadosInvalidos() throws Exception {
            final var dadosInvalidos = new Empresa();
            dadosInvalidos.setNome("");
            dadosInvalidos.setVencBombeiros(LocalDate.now());

            when(empresaService.salvar(any(Empresa.class)))
                    .thenThrow(new BusinessException("O nome da empresa não pode ser vazio."));

            mockMvc.perform(post("/api/empresas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dadosInvalidos)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("O nome da empresa não pode ser vazio."));
        }
    }

    @Nested
    @DisplayName("Testes para PUT /api/empresas/{id}")
    class AtualizarEmpresaTestes {

        @Test
        @DisplayName("Deve retornar status 200 OK e a empresa atualizada")
        @WithMockUser(roles = "USER")
        void deveRetornarOkEaEmpresaAtualizada() throws Exception {
            var dadosAtualizados = new Empresa();
            dadosAtualizados.setNome("Nome Atualizado");
            dadosAtualizados.setVencBombeiros(LocalDate.now());

            var empresaRetornadaPeloServico = new Empresa(1L, "Nome Atualizado", LocalDate.now(), null, null, null);

            when(empresaService.atualizar(eq(1L), any(Empresa.class))).thenReturn(empresaRetornadaPeloServico);

            mockMvc.perform(put("/api/empresas/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dadosAtualizados)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome", is("Nome Atualizado")));
        }
    }

    @Nested
    @DisplayName("Testes para DELETE /api/empresas/{id}")
    class DeletarEmpresaTestes {

        @Test
        @DisplayName("Deve retornar status 204 No Content quando a exclusão é bem-sucedida")
        @WithMockUser(roles = "USER")
        void deveRetornarNoContentParaExclusaoComSucesso() throws Exception {
            mockMvc.perform(delete("/api/empresas/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve retornar status 404 Not Found ao tentar deletar um ID que não existe")
        @WithMockUser(roles = "USER")
        void deveRetornarNotFoundAoTentarDeletarIdInexistente() throws Exception {
            doThrow(new ResourceNotFoundException("Empresa não encontrada")).when(empresaService).deletar(eq(99L));

            mockMvc.perform(delete("/api/empresas/99"))
                    .andExpect(status().isNotFound());
        }
    }
}
