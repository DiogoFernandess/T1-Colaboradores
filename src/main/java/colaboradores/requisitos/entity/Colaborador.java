package colaboradores.requisitos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Table(name = "colaboradores")
@Entity
@Data
public class Colaborador {

    @Id
    @Column
    private String matricula;

    @Column
    @NotBlank
    private String nome;

    @Column
    @DecimalMin(value = "0.00", message = "O salário não pode ser negativo")
    private BigDecimal salario;

    @Enumerated(EnumType.STRING)
    @Column
    private TipoColaborador tipo;

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    public TipoColaborador getTipo() {
        return tipo;
    }

    public void setTipo(TipoColaborador tipo) {
        this.tipo = tipo;
    }
}
