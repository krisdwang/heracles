package heracles.data.spring.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class MatrixPoolConfigsMetaVO {

	private String poolType;

	private List<MatrixPoolConfigMetaVO> poolConfigMetaVos = new ArrayList<MatrixPoolConfigMetaVO>();

	public void addMatrixPoolConfigMetaVO(MatrixPoolConfigMetaVO matrixPoolConfigMetaVO) {
		poolConfigMetaVos.add(matrixPoolConfigMetaVO);
	}
}
