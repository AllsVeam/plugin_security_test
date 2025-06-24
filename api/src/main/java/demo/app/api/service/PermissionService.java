package demo.app.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PermissionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<String> obtenerPermisosDesdeRoles(List<String> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }

        String inSql = String.join(",", Collections.nCopies(roleIds.size(), "?"));
        String sql = """
            SELECT p.code
            FROM m_role_permission rp
            JOIN m_permission p ON rp.permission_id = p.id
            WHERE rp.role_id IN (%s)
        """.formatted(inSql);
        return jdbcTemplate.query(
                sql,
                roleIds.toArray(),
                (rs, rowNum) -> rs.getString("code")
        );
    }
}
