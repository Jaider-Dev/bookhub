package co.edu.univalle.desarrolloIII.service_inventario.service;

import co.edu.univalle.desarrolloIII.service_inventario.enums.EstadoEjemplar;
import co.edu.univalle.desarrolloIII.service_inventario.model.Ejemplar;
import co.edu.univalle.desarrolloIII.service_inventario.repository.EjemplarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EjemplarService {

    @Autowired
    private EjemplarRepository ejemplarRepository;

    public Optional<Ejemplar> findById(Long id) {
        return ejemplarRepository.findById(id);
    }

    public Ejemplar save(Ejemplar ejemplar) {
        return ejemplarRepository.save(ejemplar);
    }


    public Optional<Ejemplar> updateEstado(Long id, EstadoEjemplar nuevoEstado) {
        Optional<Ejemplar> ejemplarOpt = ejemplarRepository.findById(id);

        if (ejemplarOpt.isPresent()) {
            Ejemplar ejemplar = ejemplarOpt.get();
            ejemplar.setEstado(nuevoEstado);
            return Optional.of(ejemplarRepository.save(ejemplar));
        }
        return Optional.empty();
    }

    public java.util.List<Ejemplar> findAll() {
        return ejemplarRepository.findAll();
    }
}