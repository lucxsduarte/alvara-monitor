package com.empresa.contabilidade.alvara_monitor.services;

import com.empresa.contabilidade.alvara_monitor.entities.Company;
import com.empresa.contabilidade.alvara_monitor.enums.companyStatusFilter;
import com.empresa.contabilidade.alvara_monitor.exceptions.BusinessException;
import com.empresa.contabilidade.alvara_monitor.exceptions.ResourceNotFoundException;
import com.empresa.contabilidade.alvara_monitor.repositories.CompanyRepository;
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
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    @Nested
    @DisplayName("Testes para o método listar()")
    class ListTests {

        @Test
        @DisplayName("Deve retornar todas as empresas quando nenhum filtro é aplicado")
        void shouldReturnAllCompaniesWhenNoFilterIsApplied() {
            final var companyList = List.of(new Company(), new Company());
            when(companyRepository.findAll()).thenReturn(companyList);

            final var result = companyService.listCompanies(null, null);

            assertNotNull(result);
            assertEquals(2, result.size());
            verify(companyRepository, times(1)).findAll();
            verify(companyRepository, never()).findByNameContainingIgnoreCase(anyString());
            verify(companyRepository, never()).findExpLicenses(any(LocalDate.class));
        }

        @Test
        @DisplayName("Deve chamar o filtro por nome quando o parâmetro nome é fornecido")
        void shouldFilterByNameWhenNameParameterIsProvided() {
            final var filterName = "Teste";
            final var filteredList = List.of(new Company());
            when(companyRepository.findByNameContainingIgnoreCase(filterName)).thenReturn(filteredList);

            final var result = companyService.listCompanies(filterName, null);

            assertEquals(1, result.size());
            verify(companyRepository, times(1)).findByNameContainingIgnoreCase(filterName);
            verify(companyRepository, never()).findAll();
            verify(companyRepository, never()).findExpLicenses(any(LocalDate.class));
        }

        @Test
        @DisplayName("Deve chamar o filtro de vencidos quando o status VENCIDOS é fornecido")
        void shouldFilterByExpiredWhenStatusIsProvided() {
            final var filterStatus = companyStatusFilter.VENCIDOS;
            final var expiredList = List.of(new Company());
            when(companyRepository.findExpLicenses(any(LocalDate.class))).thenReturn(expiredList);

            final var result = companyService.listCompanies(null, filterStatus);

            assertEquals(1, result.size());
            verify(companyRepository, times(1)).findExpLicenses(any(LocalDate.class));
            verify(companyRepository, never()).findAll();
            verify(companyRepository, never()).findByNameContainingIgnoreCase(anyString());
        }
    }

    @Nested
    @DisplayName("Testes para o método buscarPorId()")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar a empresa com sucesso quando o ID existe")
        void shouldReturnCompanyWhenIdExists() {
            final var existingId = 1L;
            final var expectedCompany = new Company();
            expectedCompany.setId(existingId);
            expectedCompany.setName("Empresa Existente");

            when(companyRepository.findById(existingId)).thenReturn(Optional.of(expectedCompany));

            final var result = companyService.findById(existingId);

            assertNotNull(result);
            assertEquals(existingId, result.getId());
            assertEquals("Empresa Existente", result.getName());

            verify(companyRepository, times(1)).findById(existingId);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o ID não existe")
        void shouldThrowExceptionWhenIdDoesNotExist() {
            final var nonExistentId = 99L;

            when(companyRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            final var exception = assertThrows(ResourceNotFoundException.class, () -> {
                companyService.findById(nonExistentId);
            });

            assertEquals("Empresa não encontrada com o id: " + nonExistentId, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes para o método salvar()")
    class SaveTests {

        @Test
        @DisplayName("Deve salvar uma empresa com sucesso quando os dados são válidos")
        void shouldSaveCompanySuccessfullyWithValidData() {
            final var companyToSave = new Company();
            companyToSave.setName("Empresa Válida");
            companyToSave.setExpLicenseFiredept(LocalDate.of(2025, 12, 31));

            final var savedCompany = new Company();
            savedCompany.setId(1L);
            savedCompany.setName("Empresa Válida");
            savedCompany.setExpLicenseFiredept(LocalDate.of(2025, 12, 31));

            when(companyRepository.save(any(Company.class))).thenReturn(savedCompany);

            final var result = companyService.save(companyToSave);
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Empresa Válida", result.getName());

            verify(companyRepository, times(1)).save(any(Company.class));
        }

        @Test
        @DisplayName("Deve lançar BusinessException ao tentar salvar empresa com nome vazio")
        void shouldThrowExceptionWhenSavingCompanyWithEmptyName() {
            final var companyWithEmptyName = new Company();
            companyWithEmptyName.setName("");
            companyWithEmptyName.setExpLicenseFiredept(LocalDate.now());

            final var exception = assertThrows(BusinessException.class, () -> {
                companyService.save(companyWithEmptyName);
            });

            assertEquals("O nome da empresa não pode ser vazio.", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar BusinessException ao tentar salvar empresa sem nenhuma data de vencimento")
        void shouldThrowExceptionWhenSavingCompanyWithNoDueDate() {
            final var companyWithoutDates = new Company();
            companyWithoutDates.setName("Empresa Válida Sem Datas");

            final var exception = assertThrows(BusinessException.class, () -> {
                companyService.save(companyWithoutDates);
            });

            assertEquals("É necessário preencher a data de vencimento de pelo menos um alvará.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes para o método atualizar()")
    class UpdateTests {

        @Test
        @DisplayName("Deve atualizar a empresa com sucesso quando ID e dados são válidos")
        void shouldUpdateCompanySuccessfullyWhenIdAndDataAreValid() {
            final var existingId = 1L;

            final var originalCompany = new Company();
            originalCompany.setId(existingId);
            originalCompany.setName("Nome Antigo");

            final var dataToUpdate = new Company();
            dataToUpdate.setName("Nome Novo e Atualizado");
            dataToUpdate.setExpLicenseFiredept(LocalDate.now());

            when(companyRepository.findById(existingId)).thenReturn(Optional.of(originalCompany));
            when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> invocation.getArgument(0));


            final var result = companyService.update(existingId, dataToUpdate);

            assertNotNull(result);
            assertEquals(existingId, result.getId());
            assertEquals("Nome Novo e Atualizado", result.getName());
            verify(companyRepository, times(1)).findById(existingId);
            verify(companyRepository, times(1)).save(originalCompany);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao tentar atualizar uma empresa que não existe")
        void lancarExcecaoAoAtualizarEmpresaInexistente() {
            final var nonExistentId = 99L;
            final var dataToUpdate = new Company();
            dataToUpdate.setName("Nome Válido");
            dataToUpdate.setExpLicenseFiredept(LocalDate.now());

            when(companyRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                companyService.update(nonExistentId, dataToUpdate);
            });

            verify(companyRepository, never()).save(any(Company.class));
        }

        @Test
        @DisplayName("Deve lançar BusinessException ao tentar atualizar com dados inválidos")
        void lancarExcecaoAoAtualizarComDadosInvalidos() {
            final var existingId = 1L;
            final var existingCompany = new Company();
            existingCompany.setId(existingId);

            final var invalidData = new Company();
            invalidData.setName("");
            invalidData.setExpLicenseFiredept(LocalDate.now());

            when(companyRepository.findById(existingId)).thenReturn(Optional.of(existingCompany));

            assertThrows(BusinessException.class, () -> {
                companyService.update(existingId, invalidData);
            });
        }
    }

    @Nested
    @DisplayName("Testes para o método deletar()")
    class DeleteTests {

        @Test
        @DisplayName("Deve deletar a empresa com sucesso quando o ID existe")
        void shouldDeleteCompanySuccessfullyWhenIdExists() {
            final var existingId = 1L;

            when(companyRepository.findById(existingId)).thenReturn(Optional.of(new Company()));

            assertDoesNotThrow(() -> {
                companyService.delete(existingId);
            });

            verify(companyRepository, times(1)).deleteById(existingId);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao tentar deletar um ID que não existe")
        void shouldThrowExceptionWhenDeletingNonExistentId() {
            final var nonExistentId = 99L;

            when(companyRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                companyService.delete(nonExistentId);
            });

            verify(companyRepository, never()).deleteById(anyLong());
        }
    }
}