package namoragroup.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import namoragroup.model.JetonRafraichissement;

public interface JetonRafraichissementRepository extends JpaRepository<JetonRafraichissement, Long> {
    Optional<JetonRafraichissement> findByIdentifiantJetonAndAdminIdAndRevokedFalse(String identifiantJeton, Long adminId);
}
