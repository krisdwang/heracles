package heracles.jdbc.spring.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AllInOnePoolConfigsVO {

	private String poolType;

	private List<AllInOnePoolConfigVO> poolConfigMetaVos = new ArrayList<AllInOnePoolConfigVO>();

	public void addMatrixPoolConfigMetaVO(AllInOnePoolConfigVO matrixPoolConfigMetaVO) {
		poolConfigMetaVos.add(matrixPoolConfigMetaVO);
	}
}
