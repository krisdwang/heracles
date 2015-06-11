package heracles.data.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class JdbcMatrixNamespaceHandlerSupport extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("jdbc-matrix", new JdbcMatrixBeanDefinitionParser());
		registerBeanDefinitionParser("matrix-datasource", new MatrixDatasourceBeanDefinitionParser());
	}
}
