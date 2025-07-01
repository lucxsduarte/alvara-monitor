package com.empresa.contabilidade.alvara_monitor.service;

import com.empresa.contabilidade.alvara_monitor.enums.FiltroStatusEmpresa;
import com.empresa.contabilidade.alvara_monitor.exception.BusinessException;
import com.empresa.contabilidade.alvara_monitor.exception.ResourceNotFoundException;
import com.empresa.contabilidade.alvara_monitor.model.Empresa;
import com.empresa.contabilidade.alvara_monitor.repository.EmpresaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @InjectMocks
    private EmpresaService empresaService;

    @Nested
    @DisplayName("Testes para o método listar()")
    class ListarTestes {

        @Test
        @DisplayName("Deve retornar todas as empresas quando nenhum filtro é aplicado")
        void listarTodasEmpresasQuandoNenhumFiltro() {
            final var listaDeEmpresas = List.of(new Empresa(), new Empresa());
            when(empresaRepository.findAll()).thenReturn(listaDeEmpresas);

            final var resultado = empresaService.listar(null, null);

            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            verify(empresaRepository, times(1)).findAll();
            verify(empresaRepository, never()).findByNomeContainingIgnoreCase(anyString());
            verify(empresaRepository, never()).findComAlvarasVencidos(any(LocalDate.class));
        }

        @Test
        @DisplayName("Deve chamar o filtro por nome quando o parâmetro nome é fornecido")
        void listarFiltroPorNome() {
            final var nomeFiltro = "Teste";
            final var listaFiltrada = List.of(new Empresa());
            when(empresaRepository.findByNomeContainingIgnoreCase(nomeFiltro)).thenReturn(listaFiltrada);

            final var resultado = empresaService.listar(nomeFiltro, null);

            assertEquals(1, resultado.size());
            verify(empresaRepository, times(1)).findByNomeContainingIgnoreCase(nomeFiltro);
            verify(empresaRepository, never()).findAll();
            verify(empresaRepository, never()).findComAlvarasVencidos(any(LocalDate.class));
        }

        @Test
        @DisplayName("Deve chamar o filtro de vencidos quando o status VENCIDOS é fornecido")
        void listarFiltroPorVencidos() {
            final var statusFiltro = FiltroStatusEmpresa.VENCIDOS;
            final var listaDeVencidos = List.of(new Empresa());
            when(empresaRepository.findComAlvarasVencidos(any(LocalDate.class))).thenReturn(listaDeVencidos);

            final var resultado = empresaService.listar(null, statusFiltro);

            assertEquals(1, resultado.size());
            verify(empresaRepository, times(1)).findComAlvarasVencidos(any(LocalDate.class));
            verify(empresaRepository, never()).findAll();
            verify(empresaRepository, never()).findByNomeContainingIgnoreCase(anyString());
        }
    }

    @Nested
    @DisplayName("Testes para o método buscarPorId()")
    class BuscarPorIdTestes {

        @Test
        @DisplayName("Deve retornar a empresa com sucesso quando o ID existe")
        void retornarEmpresaQuandoIdExiste() {
            final var idExistente = 1L;
            final var empresaEsperada = new Empresa();
            empresaEsperada.setId(idExistente);
            empresaEsperada.setNome("Empresa Existente");

            when(empresaRepository.findById(idExistente)).thenReturn(Optional.of(empresaEsperada));

            final var resultado = empresaService.buscarPorId(idExistente);

            assertNotNull(resultado);
            assertEquals(idExistente, resultado.getId());
            assertEquals("Empresa Existente", resultado.getNome());

            verify(empresaRepository, times(1)).findById(idExistente);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o ID não existe")
        void lancarExcecaoQuandoIdNaoExiste() {
            final var idInexistente = 99L;

            when(empresaRepository.findById(idInexistente)).thenReturn(Optional.empty());

            final var exception = assertThrows(ResourceNotFoundException.class, () -> {
                empresaService.buscarPorId(idInexistente);
            });

            assertEquals("Empresa não encontrada com o id: " + idInexistente, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes para o método salvar()")
    class SalvarTestes {

        @Test
        @DisplayName("Deve salvar uma empresa com sucesso quando os dados são válidos")
        void salvarEmpresaComSucesso() {
            final var empresaParaSalvar = new Empresa();
            empresaParaSalvar.setNome("Empresa Válida");
            empresaParaSalvar.setVencBombeiros(LocalDate.of(2025, 12, 31));

            final var empresaSalva = new Empresa();
            empresaSalva.setId(1L);
            empresaSalva.setNome("Empresa Válida");
            empresaSalva.setVencBombeiros(LocalDate.of(2025, 12, 31));

            when(empresaRepository.save(any(Empresa.class))).thenReturn(empresaSalva);

            final var resultado = empresaService.salvar(empresaParaSalvar);
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertEquals("Empresa Válida", resultado.getNome());

            verify(empresaRepository, times(1)).save(any(Empresa.class));
        }

        @Test
        @DisplayName("Deve lançar BusinessException ao tentar salvar empresa com nome vazio")
        void lancarExcecaoSalvarEmpresaComNomeVazio() {
            final var empresaComNomeVazio = new Empresa();
            empresaComNomeVazio.setNome("");
            empresaComNomeVazio.setVencBombeiros(LocalDate.now());

            final var exception = assertThrows(BusinessException.class, () -> {
                empresaService.salvar(empresaComNomeVazio);
            });

            assertEquals("O nome da empresa não pode ser vazio.", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar BusinessException ao tentar salvar empresa sem nenhuma data de vencimento")
        void lancarExcecaoSalvarEmpresaComDatasVencimentoNulas() {
            final var empresaSemDatas = new Empresa();
            empresaSemDatas.setNome("Empresa Válida Sem Datas");

            final var exception = assertThrows(BusinessException.class, () -> {
                empresaService.salvar(empresaSemDatas);
            });

            assertEquals("É necessário preencher a data de vencimento de pelo menos um alvará.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes para o método atualizar()")
    class AtualizarTestes {

        @Test
        @DisplayName("Deve atualizar a empresa com sucesso quando ID e dados são válidos")
        void atualizarEmpresaComSucesso() {
            final var idExistente = 1L;

            final var empresaOriginal = new Empresa();
            empresaOriginal.setId(idExistente);
            empresaOriginal.setNome("Nome Antigo");

            final var dadosParaAtualizar = new Empresa();
            dadosParaAtualizar.setNome("Nome Novo e Atualizado");
            dadosParaAtualizar.setVencBombeiros(LocalDate.now());

            when(empresaRepository.findById(idExistente)).thenReturn(Optional.of(empresaOriginal));
            when(empresaRepository.save(any(Empresa.class))).thenAnswer(invocation -> invocation.getArgument(0));


            final var resultado = empresaService.atualizar(idExistente, dadosParaAtualizar);

            assertNotNull(resultado);
            assertEquals(idExistente, resultado.getId());
            assertEquals("Nome Novo e Atualizado", resultado.getNome());
            verify(empresaRepository, times(1)).findById(idExistente);
            verify(empresaRepository, times(1)).save(empresaOriginal);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao tentar atualizar uma empresa que não existe")
        void lancarExcecaoAoAtualizarEmpresaInexistente() {
            final var idInexistente = 99L;
            final var dadosParaAtualizar = new Empresa();
            dadosParaAtualizar.setNome("Nome Válido");
            dadosParaAtualizar.setVencBombeiros(LocalDate.now());

            when(empresaRepository.findById(idInexistente)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                empresaService.atualizar(idInexistente, dadosParaAtualizar);
            });

            verify(empresaRepository, never()).save(any(Empresa.class));
        }

        @Test
        @DisplayName("Deve lançar BusinessException ao tentar atualizar com dados inválidos")
        void lancarExcecaoAoAtualizarComDadosInvalidos() {
            final var idExistente = 1L;
            final var empresaExistente = new Empresa();
            empresaExistente.setId(idExistente);

            final var dadosInvalidos = new Empresa();
            dadosInvalidos.setNome("");
            dadosInvalidos.setVencBombeiros(LocalDate.now());

            when(empresaRepository.findById(idExistente)).thenReturn(Optional.of(empresaExistente));

            assertThrows(BusinessException.class, () -> {
                empresaService.atualizar(idExistente, dadosInvalidos);
            });
        }
    }

    @Nested
    @DisplayName("Testes para o método deletar()")
    class DeletarTestes {

        @Test
        @DisplayName("Deve deletar a empresa com sucesso quando o ID existe")
        void deletarEmpresaComSucesso() {
            final var idExistente = 1L;

            when(empresaRepository.findById(idExistente)).thenReturn(Optional.of(new Empresa()));

            assertDoesNotThrow(() -> {
                empresaService.deletar(idExistente);
            });

            verify(empresaRepository, times(1)).deleteById(idExistente);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao tentar deletar um ID que não existe")
        void lancarExcecaoAoDeletarIdInexistente() {
            final var idInexistente = 99L;

            when(empresaRepository.findById(idInexistente)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                empresaService.deletar(idInexistente);
            });

            verify(empresaRepository, never()).deleteById(anyLong());
        }
    }
}