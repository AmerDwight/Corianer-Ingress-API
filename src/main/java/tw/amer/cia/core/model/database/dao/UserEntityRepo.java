package tw.amer.cia.core.model.database.dao;

import tw.amer.cia.core.model.database.UserEntity;
import tw.amer.cia.core.model.pojo.service.host.web.manager.roleManagement.Web_RoleManagementMemberDto;
import tw.amer.cia.core.model.pojo.service.host.web.manager.roleManagement.Web_RoleManagementMemberSimpleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserEntityRepo extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByUserId(String userId);

    List<UserEntity> findByUserIdIn(Collection<String> userId);

    List<UserEntity> findByDeptCode(String deptCode);

    @Query(" SELECT cuu FROM UserEntity cuu " +
            " WHERE EXISTS ( " +
            "      SELECT 1 FROM RoleUserEntity cru " +
            "           WHERE cru.userId = cuu.userId" +
            "           AND   cru.roleId = :roleId  ) ")
    List<UserEntity> findByRoleId(@Param("roleId") String roleId);


    @Query(" SELECT new tw.amer.cia.core.model.pojo.service.host.web.manager.roleManagement.Web_RoleManagementMemberDto" +
            "(cuu.userId, cuu.deptCode, cuu.userName, cru.isUiVisible) " +
            " FROM UserEntity cuu " +
            " INNER JOIN RoleUserEntity cru ON cuu.userId = cru.userId " +
            " WHERE cuu.isActive = 'Y' " +
            " AND cru.roleId = :roleId ")
    List<Web_RoleManagementMemberDto> findRoleManagementMemberByRoleId(String roleId);

    @Query("SELECT DISTINCT cuu.deptCode FROM UserEntity cuu WHERE cuu.deptCode != null ")
    List<String> findAllDistinctDeptCode();

    // CIA 4.3.0
    @Query("SELECT DISTINCT cuu.deptCode FROM UserEntity cuu WHERE cuu.deptCode LIKE :deptCode")
    Page<String> findDeptCodeByDeptCodeLike(Pageable pageable, @Param("deptCode") String deptCode);

    // CIA 4.3.0
    @Query("SELECT DISTINCT new tw.amer.cia.core.model.pojo.service.host.web.manager.roleManagement.Web_RoleManagementMemberSimpleDto" +
            "( cuu.userId, cuu.deptCode, cuu.userName ) " +
            "FROM UserEntity cuu " +
            "WHERE cuu.deptCode = :deptCode " +
            "AND cuu.isActive = 'Y' ")
    Page<Web_RoleManagementMemberSimpleDto> findMembersByDeptCode(Pageable pageable, @Param("deptCode") String deptCode);

    @Query("SELECT u.userId FROM UserEntity u WHERE u.userId IN :userIds")
    Collection<String> findUserIdsByUserIdIn(@Param("userIds") Collection<String> userIds);

    @Query("SELECT u FROM UserEntity u WHERE u.userId LIKE :userId ORDER BY u.userId")
    List<UserEntity> findByUserIdLikeOrderByUserId(String userId);
}
