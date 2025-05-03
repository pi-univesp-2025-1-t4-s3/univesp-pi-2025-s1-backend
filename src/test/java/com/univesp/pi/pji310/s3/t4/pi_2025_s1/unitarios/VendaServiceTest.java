package com.univesp.pi.pji310.s3.t4.pi_2025_s1.unitarios;

import com.univesp.pi.pji310.s3.t4.pi_2025_s1.domain.business.*;
import com.univesp.pi.pji310.s3.t4.pi_2025_s1.repository.business.VendaRepository;
import com.univesp.pi.pji310.s3.t4.pi_2025_s1.service.domain.EstoqueService;
import com.univesp.pi.pji310.s3.t4.pi_2025_s1.service.domain.VendaService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VendaServiceTest {

    @InjectMocks
    private VendaService vendaService;

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private EstoqueService estoqueService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCriarVendaComDescontoNoEstoque() {
        Produto produto = new Produto(1L, "Produto", "Desc", new BigDecimal("10.00"));
        ItemVenda item = new ItemVenda(null, 2, null, produto);
        Venda venda = new Venda(null, LocalDate.now(), List.of(item));

        Venda salvo = new Venda(1L, venda.getData(), venda.getItens());
        when(vendaRepository.save(any(Venda.class))).thenReturn(salvo);

        Venda resultado = vendaService.salvar(venda);

        assertEquals(1L, resultado.getId());
        verify(estoqueService).decrementarQuantidadePorProduto(1L, 2);
    }

    @Test
    void deveBuscarVendaPorId() {
        Venda venda = new Venda(1L, LocalDate.now(), List.of());
        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));

        Venda resultado = vendaService.buscarPorId(1L);

        assertEquals(1L, resultado.getId());
    }

    @Test
    void deveLancarExcecaoSeVendaNaoEncontrada() {
        when(vendaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vendaService.buscarPorId(1L));
    }

    @Test
    void deveAtualizarVendaEAjustarEstoque() {
        Produto produto = new Produto(1L, "Produto", "Desc", new BigDecimal("10.00"));
        ItemVenda itemOriginal = new ItemVenda(1L, 2, null, produto);
        Venda existente = new Venda(1L, LocalDate.now(), new ArrayList<>(List.of(itemOriginal)));

        ItemVenda itemNovo = new ItemVenda(1L, 3, null, produto);
        Venda atualizada = new Venda(1L, LocalDate.now(), new ArrayList<>(List.of(itemNovo)));

        when(vendaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(vendaRepository.save(any(Venda.class))).thenReturn(atualizada);

        Venda resultado = vendaService.atualizar(1L, atualizada);

        assertEquals(3, resultado.getItens().get(0).getQuantidade());
        verify(estoqueService).incrementarQuantidadePorProduto(1L, 2);
        verify(estoqueService).decrementarQuantidadePorProduto(1L, 3);
    }
}
