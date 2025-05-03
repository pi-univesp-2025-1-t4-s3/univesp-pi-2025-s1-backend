package com.univesp.pi.pji310.s3.t4.pi_2025_s1.domain.business;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade mínima é 1")
    private Integer quantidade;

    @ManyToOne
    @JoinColumn(name = "venda_id")
    @JsonBackReference
    @ToString.Exclude
    private Venda venda;

    @NotNull(message = "Produto é obrigatório")
    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;
}
