package tw.amer.cia.core.component.structural.jpa.specification;

import tw.amer.cia.core.model.database.FabEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class FabEntitySpecification
{
    public static Specification<FabEntity> fabIdIn(List<String> fabIds)
    {
        return (root, query, criteriaBuilder) ->
        {
            if (fabIds == null || fabIds.isEmpty())
            {
                return null;
            }
            return root.get("fabId").in(fabIds);
        };
    }

    public static Specification<FabEntity> siteIn(List<String> sites)
    {
        return (root, query, criteriaBuilder) ->
        {
            if (sites == null || sites.isEmpty())
            {
                return null;
            }
            return root.get("site").in(sites);
        };
    }
}
