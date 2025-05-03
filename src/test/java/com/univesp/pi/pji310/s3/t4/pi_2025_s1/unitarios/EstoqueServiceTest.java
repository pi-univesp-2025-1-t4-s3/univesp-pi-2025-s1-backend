package com.univesp.pi.pji310.s3.t4.pi_2025_s1.unitarios;

import com.univesp.pi.pji310.s3.t4.pi_2025_s1.domain.business.Estoque;
import com.univesp.pi.pji310.s3.t4.pi_2025_s1.domain.business.Produto;
import com.univesp.pi.pji310.s3.t4.pi_2025_s1.repository.business.EstoqueRepository;
import com.univesp.pi.pji310.s3.t4.pi_2025_s1.service.domain.EstoqueService;
import com.univesp.pi.pji310.s3.t4.pi_2025_s1.service.domain.ProdutoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EstoqueServiceTest {

    @InjectMocks
    private EstoqueService estoqueService;

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private ProdutoService produtoService;

    private Produto produto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        produto = new Produto(1L, "Produto Teste", "Descrição", new BigDecimal("10.00"));
    }

    @Test
    void deveListarTodosOsEstoques() {
        List<Estoque> lista = List.of(new Estoque());
        when(estoqueRepository.findAll()).thenReturn(lista);

        List<Estoque> resultado = estoqueService.listarTodos();

        assertEquals(1, resultado.size());
    }

    @Test
    void deveBuscarEstoquePorProdutoId() {
        Estoque estoque = new Estoque();
        estoque.setProduto(produto);
        estoque.setQuantidade(5);

        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(estoque));

        Estoque resultado = estoqueService.buscarPorProdutoId(1L);

        assertEquals(5, resultado.getQuantidade());
        assertEquals(produto, resultado.getProduto());
    }

    @Test
    void deveLancarExcecaoSeEstoqueNaoExistirAoBuscar() {
        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> estoqueService.buscarPorProdutoId(1L));
    }

    @Test
    void deveIncrementarQuantidadeComEstoqueExistente() {
        Estoque estoque = new Estoque();
        estoque.setProduto(produto);
        estoque.setQuantidade(5);

        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(estoque));

        estoqueService.incrementarQuantidadePorProduto(1L, 3);

        assertEquals(8, estoque.getQuantidade());
        verify(estoqueRepository).save(estoque);
    }

    @Test
    void deveIncrementarCriandoNovoEstoqueSeNaoExistir() {
        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.empty());
        when(produtoService.buscarPorId(1L)).thenReturn(produto);

        estoqueService.incrementarQuantidadePorProduto(1L, 4);

        ArgumentCaptor<Estoque> captor = ArgumentCaptor.forClass(Estoque.class);
        verify(estoqueRepository).save(captor.capture());

        Estoque salvo = captor.getValue();
        assertEquals(4, salvo.getQuantidade());
        assertEquals(produto, salvo.getProduto());
    }

    @Test
    void deveDecrementarQuantidadeComEstoqueExistente() {
        Estoque estoque = new Estoque();
        estoque.setProduto(produto);
        estoque.setQuantidade(10);

        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(estoque));

        estoqueService.decrementarQuantidadePorProduto(1L, 4);

        assertEquals(6, estoque.getQuantidade());
        verify(estoqueRepository).save(estoque);
    }

    @Test
    void deveDecrementarCriandoNovoEstoqueSeNaoExistirComQuantidadeZero() {
        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.empty());
        when(produtoService.buscarPorId(1L)).thenReturn(produto);

        assertThrows(IllegalArgumentException.class, () ->
                estoqueService.decrementarQuantidadePorProduto(1L, 1));
    }

    @Test
    void deveLancarExcecaoSeDecrementarMaisQueDisponivel() {
        Estoque estoque = new Estoque();
        estoque.setProduto(produto);
        estoque.setQuantidade(2);

        when(estoqueRepository.findByProdutoId(1L)).thenReturn(Optional.of(estoque));

        assertThrows(IllegalArgumentException.class, () ->
                estoqueService.decrementarQuantidadePorProduto(1L, 5));
    }
}
