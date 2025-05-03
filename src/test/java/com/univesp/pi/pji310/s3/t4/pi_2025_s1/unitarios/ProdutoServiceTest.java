package com.univesp.pi.pji310.s3.t4.pi_2025_s1.unitarios;

import com.univesp.pi.pji310.s3.t4.pi_2025_s1.domain.business.Produto;
import com.univesp.pi.pji310.s3.t4.pi_2025_s1.repository.business.ProdutoRepository;
import com.univesp.pi.pji310.s3.t4.pi_2025_s1.service.domain.ProdutoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {

    @InjectMocks
    private ProdutoService produtoService;

    @Mock
    private ProdutoRepository produtoRepository;

    private Produto produto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        produto = new Produto(1L, "Produto Teste", "Descrição", new BigDecimal("10.00"));
    }

    @Test
    void deveSalvarProduto() {
        when(produtoRepository.save(produto)).thenReturn(produto);

        Produto salvo = produtoService.salvar(produto);

        assertNotNull(salvo);
        assertEquals(produto.getId(), salvo.getId());
        verify(produtoRepository).save(produto);
    }

    @Test
    void deveBuscarProdutoPorId() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        Produto resultado = produtoService.buscarPorId(1L);

        assertEquals(produto.getId(), resultado.getId());
    }

    @Test
    void deveLancarExcecaoAoBuscarProdutoInexistente() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> produtoService.buscarPorId(1L));
    }

    @Test
    void deveListarTodosProdutos() {
        List<Produto> lista = List.of(produto);
        when(produtoRepository.findAll()).thenReturn(lista);

        List<Produto> resultado = produtoService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals(produto.getId(), resultado.get(0).getId());
    }

    @Test
    void deveExcluirProdutoSeExistir() {
        when(produtoRepository.existsById(1L)).thenReturn(true);

        produtoService.excluirPorId(1L);

        verify(produtoRepository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoExcluirProdutoInexistente() {
        when(produtoRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> produtoService.excluirPorId(1L));
    }
}
