

import mz.banco.wsd.entity.Cliente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClienteTest {

    @Test
    void testarCliente() {

        Cliente cliente = new Cliente();

        cliente.setNome("Maria");
        cliente.setTelefone("841234567");
        cliente.setEmail("maria@gmail.com");

        assertEquals("Maria", cliente.getNome());
        assertEquals("841234567", cliente.getTelefone());
        assertEquals("maria@gmail.com", cliente.getEmail());
        assertEquals("activo", cliente.getEstado());
    }
}
