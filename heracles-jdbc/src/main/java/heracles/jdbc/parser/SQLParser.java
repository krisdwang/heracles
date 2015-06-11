package heracles.jdbc.parser;

import heracles.jdbc.parser.exception.SQLParserException;
import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.NoViableAltException;
import antlr.ParserSharedInputState;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenBuffer;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.collections.AST;
import antlr.collections.impl.ASTArray;
import antlr.collections.impl.BitSet;

public class SQLParser extends antlr.LLkParser implements SQLTokenTypes {

	protected SQLParser(TokenBuffer tokenBuf, int k) {
		super(tokenBuf, k);
		tokenNames = _tokenNames;
		buildTokenTypeASTClassMap();
		astFactory = new ASTFactory(getTokenTypeToASTClassMap());
	}

	public SQLParser(TokenBuffer tokenBuf) {
		this(tokenBuf, 3);
	}

	protected SQLParser(TokenStream lexer, int k) {
		super(lexer, k);
		tokenNames = _tokenNames;
		buildTokenTypeASTClassMap();
		astFactory = new ASTFactory(getTokenTypeToASTClassMap());
	}

	public SQLParser(TokenStream lexer) {
		this(lexer, 3);
	}

	public SQLParser(ParserSharedInputState state) {
		super(state, 3);
		tokenNames = _tokenNames;
		buildTokenTypeASTClassMap();
		astFactory = new ASTFactory(getTokenTypeToASTClassMap());
	}

	public final void statement() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statement_AST = null;

		try { // for error handling
			{
				switch (LA(1)) {
				case SELECT:
				case OPEN_COMMENT: {
					selectStatement();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case INSERT: {
					insertStatement();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case DELETE: {
					deleteStatement();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case UPDATE: {
					updateStatement();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default: {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			AST tmp1_AST = null;
			tmp1_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp1_AST);
			match(Token.EOF_TYPE);
			statement_AST = (AST) currentAST.root;
		} catch (Throwable e) {
			throw new SQLParserException(e);
		}
		returnAST = statement_AST;
	}

	public final void selectStatement() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectStatement_AST = null;

		selectRoot();
		astFactory.addASTChild(currentAST, returnAST);
		selectStatement_AST = (AST) currentAST.root;

		selectStatement_AST = (AST) astFactory.make((new ASTArray(2))
				.add(astFactory.create(SELECT_ROOT, "select_root")).add(selectStatement_AST));

		currentAST.root = selectStatement_AST;
		currentAST.child = selectStatement_AST != null && selectStatement_AST.getFirstChild() != null ? selectStatement_AST
				.getFirstChild() : selectStatement_AST;
		currentAST.advanceChildToEnd();
		selectStatement_AST = (AST) currentAST.root;
		returnAST = selectStatement_AST;
	}

	public final void insertStatement() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST insertStatement_AST = null;

		insertRoot();
		astFactory.addASTChild(currentAST, returnAST);
		insertStatement_AST = (AST) currentAST.root;

		insertStatement_AST = (AST) astFactory.make((new ASTArray(2))
				.add(astFactory.create(INSERT_ROOT, "insert_root")).add(insertStatement_AST));

		currentAST.root = insertStatement_AST;
		currentAST.child = insertStatement_AST != null && insertStatement_AST.getFirstChild() != null ? insertStatement_AST
				.getFirstChild() : insertStatement_AST;
		currentAST.advanceChildToEnd();
		insertStatement_AST = (AST) currentAST.root;
		returnAST = insertStatement_AST;
	}

	public final void deleteStatement() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST deleteStatement_AST = null;

		deleteRoot();
		astFactory.addASTChild(currentAST, returnAST);
		deleteStatement_AST = (AST) currentAST.root;

		deleteStatement_AST = (AST) astFactory.make((new ASTArray(2))
				.add(astFactory.create(DELETE_ROOT, "delete_root")).add(deleteStatement_AST));

		currentAST.root = deleteStatement_AST;
		currentAST.child = deleteStatement_AST != null && deleteStatement_AST.getFirstChild() != null ? deleteStatement_AST
				.getFirstChild() : deleteStatement_AST;
		currentAST.advanceChildToEnd();
		deleteStatement_AST = (AST) currentAST.root;
		returnAST = deleteStatement_AST;
	}

	public final void updateStatement() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST updateStatement_AST = null;

		updateRoot();
		astFactory.addASTChild(currentAST, returnAST);
		updateStatement_AST = (AST) currentAST.root;

		updateStatement_AST = (AST) astFactory.make((new ASTArray(2))
				.add(astFactory.create(UPDATE_ROOT, "update_root")).add(updateStatement_AST));

		currentAST.root = updateStatement_AST;
		currentAST.child = updateStatement_AST != null && updateStatement_AST.getFirstChild() != null ? updateStatement_AST
				.getFirstChild() : updateStatement_AST;
		currentAST.advanceChildToEnd();
		updateStatement_AST = (AST) currentAST.root;
		returnAST = updateStatement_AST;
	}

	public final void commentStatement() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST commentStatement_AST = null;

		match(OPEN_COMMENT);
		hintStatement();
		astFactory.addASTChild(currentAST, returnAST);
		match(CLOSE_COMMENT);
		commentStatement_AST = (AST) currentAST.root;
		returnAST = commentStatement_AST;
	}

	public final void hintStatement() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST hintStatement_AST = null;

		match(HINT);
		AST tmp5_AST = null;
		tmp5_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp5_AST);
		match(FORCE_READ);
		hintStatement_AST = (AST) currentAST.root;
		returnAST = hintStatement_AST;
	}

	public final void selectRoot() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectRoot_AST = null;

		{
			switch (LA(1)) {
			case OPEN_COMMENT: {
				commentStatement();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case SELECT: {
				break;
			}
			default: {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		selectClause();
		astFactory.addASTChild(currentAST, returnAST);
		fromClause();
		astFactory.addASTChild(currentAST, returnAST);
		whereClause();
		astFactory.addASTChild(currentAST, returnAST);
		{
			switch (LA(1)) {
			case ORDER: {
				orderByClause();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case LIMIT: {
				break;
			}
			default: {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		{
			switch (LA(1)) {
			case LIMIT: {
				limitClause();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF: {
				break;
			}
			default: {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		selectRoot_AST = (AST) currentAST.root;
		returnAST = selectRoot_AST;
	}

	public final void selectClause() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectClause_AST = null;

		AST tmp6_AST = null;
		tmp6_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp6_AST);
		match(SELECT);
		selectList();
		astFactory.addASTChild(currentAST, returnAST);
		selectClause_AST = (AST) currentAST.root;
		returnAST = selectClause_AST;
	}

	public final void fromClause() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fromClause_AST = null;

		AST tmp7_AST = null;
		tmp7_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp7_AST);
		match(FROM);
		aliasedExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
			_loop34: do {
				if ((_tokenSet_0.member(LA(1)))) {
					joinClause();
					astFactory.addASTChild(currentAST, returnAST);
				} else {
					break _loop34;
				}

			} while (true);
		}
		fromClause_AST = (AST) currentAST.root;
		returnAST = fromClause_AST;
	}

	public final void whereClause() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST whereClause_AST = null;

		AST tmp8_AST = null;
		tmp8_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp8_AST);
		match(WHERE);
		logicalExpression();
		astFactory.addASTChild(currentAST, returnAST);
		whereClause_AST = (AST) currentAST.root;
		returnAST = whereClause_AST;
	}

	public final void orderByClause() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST orderByClause_AST = null;

		AST tmp9_AST = null;
		tmp9_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp9_AST);
		match(ORDER);
		match(BY);
		orderByExpr();
		astFactory.addASTChild(currentAST, returnAST);
		{
			_loop43: do {
				if ((LA(1) == COMMA)) {
					AST tmp11_AST = null;
					tmp11_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp11_AST);
					match(COMMA);
					orderByExpr();
					astFactory.addASTChild(currentAST, returnAST);
				} else {
					break _loop43;
				}

			} while (true);
		}
		orderByClause_AST = (AST) currentAST.root;
		returnAST = orderByClause_AST;
	}

	public final void limitClause() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST limitClause_AST = null;

		AST tmp12_AST = null;
		tmp12_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp12_AST);
		match(LIMIT);
		AST tmp13_AST = null;
		tmp13_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp13_AST);
		match(NUMERICAL);
		{
			switch (LA(1)) {
			case COMMA: {
				match(COMMA);
				AST tmp15_AST = null;
				tmp15_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp15_AST);
				match(NUMERICAL);
				break;
			}
			case EOF: {
				break;
			}
			default: {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		limitClause_AST = (AST) currentAST.root;
		returnAST = limitClause_AST;
	}

	public final void selectList() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectList_AST = null;

		{
			switch (LA(1)) {
			case STAR: {
				AST tmp16_AST = null;
				tmp16_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp16_AST);
				match(STAR);
				break;
			}
			case MAX:
			case MIN:
			case AVG:
			case SUM:
			case COUNT:
			case IDENT: {
				selectExpression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default: {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		{
			_loop14: do {
				if ((LA(1) == COMMA)) {
					AST tmp17_AST = null;
					tmp17_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp17_AST);
					match(COMMA);
					selectExpression();
					astFactory.addASTChild(currentAST, returnAST);
				} else {
					break _loop14;
				}

			} while (true);
		}
		selectList_AST = (AST) currentAST.root;
		returnAST = selectList_AST;
	}

	public final void selectExpression() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectExpression_AST = null;

		switch (LA(1)) {
		case IDENT: {
			aliasedExpression();
			astFactory.addASTChild(currentAST, returnAST);
			selectExpression_AST = (AST) currentAST.root;
			break;
		}
		case MAX: {
			maxFunc();
			astFactory.addASTChild(currentAST, returnAST);
			selectExpression_AST = (AST) currentAST.root;
			break;
		}
		case MIN: {
			minFunc();
			astFactory.addASTChild(currentAST, returnAST);
			selectExpression_AST = (AST) currentAST.root;
			break;
		}
		case SUM: {
			sumFunc();
			astFactory.addASTChild(currentAST, returnAST);
			selectExpression_AST = (AST) currentAST.root;
			break;
		}
		case AVG: {
			avgFunc();
			astFactory.addASTChild(currentAST, returnAST);
			selectExpression_AST = (AST) currentAST.root;
			break;
		}
		case COUNT: {
			countFunc();
			astFactory.addASTChild(currentAST, returnAST);
			selectExpression_AST = (AST) currentAST.root;
			break;
		}
		default: {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = selectExpression_AST;
	}

	public final void aliasedExpression() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST aliasedExpression_AST = null;

		AST tmp18_AST = null;
		tmp18_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp18_AST);
		match(IDENT);
		{
			switch (LA(1)) {
			case IDENT: {
				AST tmp19_AST = null;
				tmp19_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp19_AST);
				match(IDENT);
				break;
			}
			case AS: {
				{
					AST tmp20_AST = null;
					tmp20_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp20_AST);
					match(AS);
					AST tmp21_AST = null;
					tmp21_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp21_AST);
					match(IDENT);
				}
				break;
			}
			case FROM:
			case SET:
			case WHERE:
			case LEFT:
			case RIGHT:
			case INNER:
			case JOIN:
			case ON:
			case COMMA: {
				break;
			}
			default: {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		aliasedExpression_AST = (AST) currentAST.root;
		returnAST = aliasedExpression_AST;
	}

	public final void maxFunc() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST maxFunc_AST = null;

		AST tmp22_AST = null;
		tmp22_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp22_AST);
		match(MAX);
		match(OPEN);
		AST tmp24_AST = null;
		tmp24_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp24_AST);
		match(IDENT);
		match(CLOSE);
		{
			switch (LA(1)) {
			case AS:
			case IDENT: {
				aliasedSuffix();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case FROM:
			case COMMA: {
				break;
			}
			default: {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		maxFunc_AST = (AST) currentAST.root;
		returnAST = maxFunc_AST;
	}

	public final void minFunc() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST minFunc_AST = null;

		AST tmp26_AST = null;
		tmp26_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp26_AST);
		match(MIN);
		match(OPEN);
		AST tmp28_AST = null;
		tmp28_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp28_AST);
		match(IDENT);
		match(CLOSE);
		{
			switch (LA(1)) {
			case AS:
			case IDENT: {
				aliasedSuffix();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case FROM:
			case COMMA: {
				break;
			}
			default: {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		minFunc_AST = (AST) currentAST.root;
		returnAST = minFunc_AST;
	}

	public final void sumFunc() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST sumFunc_AST = null;

		AST tmp30_AST = null;
		tmp30_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp30_AST);
		match(SUM);
		match(OPEN);
		AST tmp32_AST = null;
		tmp32_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp32_AST);
		match(IDENT);
		match(CLOSE);
		{
			switch (LA(1)) {
			case AS:
			case IDENT: {
				aliasedSuffix();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case FROM:
			case COMMA: {
				break;
			}
			default: {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		sumFunc_AST = (AST) currentAST.root;
		returnAST = sumFunc_AST;
	}

	public final void avgFunc() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST avgFunc_AST = null;

		AST tmp34_AST = null;
		tmp34_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp34_AST);
		match(AVG);
		match(OPEN);
		AST tmp36_AST = null;
		tmp36_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp36_AST);
		match(IDENT);
		match(CLOSE);
		{
			switch (LA(1)) {
			case AS:
			case IDENT: {
				aliasedSuffix();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case FROM:
			case COMMA: {
				break;
			}
			default: {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		avgFunc_AST = (AST) currentAST.root;
		returnAST = avgFunc_AST;
	}

	public final void countFunc() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST countFunc_AST = null;

		AST tmp38_AST = null;
		tmp38_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp38_AST);
		match(COUNT);
		match(OPEN);
		{
			switch (LA(1)) {
			case IDENT: {
				AST tmp40_AST = null;
				tmp40_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp40_AST);
				match(IDENT);
				break;
			}
			case STAR: {
				AST tmp41_AST = null;
				tmp41_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp41_AST);
				match(STAR);
				break;
			}
			default: {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		match(CLOSE);
		{
			switch (LA(1)) {
			case AS:
			case IDENT: {
				aliasedSuffix();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case FROM:
			case COMMA: {
				break;
			}
			default: {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		countFunc_AST = (AST) currentAST.root;
		returnAST = countFunc_AST;
	}

	public final void aliasedSuffix() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST aliasedSuffix_AST = null;

		switch (LA(1)) {
		case IDENT: {
			AST tmp43_AST = null;
			tmp43_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp43_AST);
			match(IDENT);
			aliasedSuffix_AST = (AST) currentAST.root;
			break;
		}
		case AS: {
			{
				AST tmp44_AST = null;
				tmp44_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp44_AST);
				match(AS);
				AST tmp45_AST = null;
				tmp45_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp45_AST);
				match(IDENT);
			}
			aliasedSuffix_AST = (AST) currentAST.root;
			break;
		}
		default: {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = aliasedSuffix_AST;
	}

	public final void joinClause() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST joinClause_AST = null;

		switch (LA(1)) {
		case COMMA: {
			AST tmp46_AST = null;
			tmp46_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp46_AST);
			match(COMMA);
			aliasedExpression();
			astFactory.addASTChild(currentAST, returnAST);
			joinClause_AST = (AST) currentAST.root;
			break;
		}
		case LEFT: {
			AST tmp47_AST = null;
			tmp47_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp47_AST);
			match(LEFT);
			{
				switch (LA(1)) {
				case OUTER: {
					match(OUTER);
					break;
				}
				case JOIN: {
					break;
				}
				default: {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			match(JOIN);
			aliasedExpression();
			astFactory.addASTChild(currentAST, returnAST);
			onClause();
			astFactory.addASTChild(currentAST, returnAST);
			joinClause_AST = (AST) currentAST.root;
			break;
		}
		case RIGHT: {
			AST tmp50_AST = null;
			tmp50_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp50_AST);
			match(RIGHT);
			{
				switch (LA(1)) {
				case OUTER: {
					match(OUTER);
					break;
				}
				case JOIN: {
					break;
				}
				default: {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			match(JOIN);
			aliasedExpression();
			astFactory.addASTChild(currentAST, returnAST);
			onClause();
			astFactory.addASTChild(currentAST, returnAST);
			joinClause_AST = (AST) currentAST.root;
			break;
		}
		case INNER:
		case JOIN: {
			{
				switch (LA(1)) {
				case INNER: {
					match(INNER);
					break;
				}
				case JOIN: {
					break;
				}
				default: {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			AST tmp54_AST = null;
			tmp54_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp54_AST);
			match(JOIN);
			aliasedExpression();
			astFactory.addASTChild(currentAST, returnAST);
			onClause();
			astFactory.addASTChild(currentAST, returnAST);
			joinClause_AST = (AST) currentAST.root;
			break;
		}
		default: {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = joinClause_AST;
	}

	public final void onClause() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST onClause_AST = null;

		AST tmp55_AST = null;
		tmp55_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp55_AST);
		match(ON);
		logicalExpression();
		astFactory.addASTChild(currentAST, returnAST);
		onClause_AST = (AST) currentAST.root;
		returnAST = onClause_AST;
	}

	public final void logicalExpression() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalExpression_AST = null;

		expression();
		astFactory.addASTChild(currentAST, returnAST);
		logicalExpression_AST = (AST) currentAST.root;
		returnAST = logicalExpression_AST;
	}

	public final void orderByExpr() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST orderByExpr_AST = null;

		AST tmp56_AST = null;
		tmp56_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp56_AST);
		match(IDENT);
		{
			switch (LA(1)) {
			case ASC: {
				AST tmp57_AST = null;
				tmp57_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp57_AST);
				match(ASC);
				break;
			}
			case DESC: {
				AST tmp58_AST = null;
				tmp58_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp58_AST);
				match(DESC);
				break;
			}
			case EOF:
			case LIMIT:
			case COMMA: {
				break;
			}
			default: {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		orderByExpr_AST = (AST) currentAST.root;
		returnAST = orderByExpr_AST;
	}

	public final void insertRoot() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST insertRoot_AST = null;

		insertClause();
		astFactory.addASTChild(currentAST, returnAST);
		columnList();
		astFactory.addASTChild(currentAST, returnAST);
		valuesClause();
		astFactory.addASTChild(currentAST, returnAST);
		insertRoot_AST = (AST) currentAST.root;
		returnAST = insertRoot_AST;
	}

	public final void insertClause() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST insertClause_AST = null;

		AST tmp59_AST = null;
		tmp59_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp59_AST);
		match(INSERT);
		{
			switch (LA(1)) {
			case INTO: {
				match(INTO);
				break;
			}
			case IDENT: {
				break;
			}
			default: {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		AST tmp61_AST = null;
		tmp61_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp61_AST);
		match(IDENT);
		insertClause_AST = (AST) currentAST.root;
		returnAST = insertClause_AST;
	}

	public final void columnList() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST columnList_AST = null;

		match(OPEN);
		AST tmp63_AST = null;
		tmp63_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp63_AST);
		match(IDENT);
		{
			_loop54: do {
				if ((LA(1) == COMMA)) {
					match(COMMA);
					AST tmp65_AST = null;
					tmp65_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp65_AST);
					match(IDENT);
				} else {
					break _loop54;
				}

			} while (true);
		}
		match(CLOSE);
		columnList_AST = (AST) currentAST.root;

		columnList_AST = (AST) astFactory.make((new ASTArray(2)).add(astFactory.create(COLUMN_LIST, "column_list"))
				.add(columnList_AST));

		currentAST.root = columnList_AST;
		currentAST.child = columnList_AST != null && columnList_AST.getFirstChild() != null ? columnList_AST
				.getFirstChild() : columnList_AST;
		currentAST.advanceChildToEnd();
		columnList_AST = (AST) currentAST.root;
		returnAST = columnList_AST;
	}

	public final void valuesClause() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST valuesClause_AST = null;

		{
			switch (LA(1)) {
			case VALUES: {
				AST tmp67_AST = null;
				tmp67_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp67_AST);
				match(VALUES);
				break;
			}
			case VALUE: {
				AST tmp68_AST = null;
				tmp68_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp68_AST);
				match(VALUE);
				break;
			}
			default: {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		match(OPEN);
		variable();
		astFactory.addASTChild(currentAST, returnAST);
		{
			_loop58: do {
				if ((LA(1) == COMMA)) {
					match(COMMA);
					variable();
					astFactory.addASTChild(currentAST, returnAST);
				} else {
					break _loop58;
				}

			} while (true);
		}
		match(CLOSE);
		valuesClause_AST = (AST) currentAST.root;
		returnAST = valuesClause_AST;
	}

	public final void variable() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST variable_AST = null;

		switch (LA(1)) {
		case NUMERICAL: {
			AST tmp72_AST = null;
			tmp72_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp72_AST);
			match(NUMERICAL);
			variable_AST = (AST) currentAST.root;
			break;
		}
		case QUOTED_STRING: {
			AST tmp73_AST = null;
			tmp73_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp73_AST);
			match(QUOTED_STRING);
			variable_AST = (AST) currentAST.root;
			break;
		}
		case PARAM: {
			AST tmp74_AST = null;
			tmp74_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp74_AST);
			match(PARAM);
			variable_AST = (AST) currentAST.root;
			break;
		}
		default: {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = variable_AST;
	}

	public final void deleteRoot() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST deleteRoot_AST = null;

		deleteClause();
		astFactory.addASTChild(currentAST, returnAST);
		whereClause();
		astFactory.addASTChild(currentAST, returnAST);
		deleteRoot_AST = (AST) currentAST.root;
		returnAST = deleteRoot_AST;
	}

	public final void deleteClause() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST deleteClause_AST = null;

		AST tmp75_AST = null;
		tmp75_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp75_AST);
		match(DELETE);
		match(FROM);
		AST tmp77_AST = null;
		tmp77_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp77_AST);
		match(IDENT);
		deleteClause_AST = (AST) currentAST.root;
		returnAST = deleteClause_AST;
	}

	public final void expression() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expression_AST = null;

		logicalOrExpression();
		astFactory.addASTChild(currentAST, returnAST);
		expression_AST = (AST) currentAST.root;
		returnAST = expression_AST;
	}

	public final void updateRoot() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST updateRoot_AST = null;

		updateClause();
		astFactory.addASTChild(currentAST, returnAST);
		setClause();
		astFactory.addASTChild(currentAST, returnAST);
		whereClause();
		astFactory.addASTChild(currentAST, returnAST);
		updateRoot_AST = (AST) currentAST.root;
		returnAST = updateRoot_AST;
	}

	public final void updateClause() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST updateClause_AST = null;

		AST tmp78_AST = null;
		tmp78_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp78_AST);
		match(UPDATE);
		aliasedExpression();
		astFactory.addASTChild(currentAST, returnAST);
		updateClause_AST = (AST) currentAST.root;
		returnAST = updateClause_AST;
	}

	public final void setClause() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST setClause_AST = null;

		AST tmp79_AST = null;
		tmp79_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp79_AST);
		match(SET);
		equalityExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
			_loop68: do {
				if ((LA(1) == COMMA)) {
					match(COMMA);
					equalityExpression();
					astFactory.addASTChild(currentAST, returnAST);
				} else {
					break _loop68;
				}

			} while (true);
		}
		setClause_AST = (AST) currentAST.root;
		returnAST = setClause_AST;
	}

	public final void equalityExpression() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST equalityExpression_AST = null;

		if ((LA(1) == IDENT) && (LA(2) == EQ)) {
			equalsToExpression();
			astFactory.addASTChild(currentAST, returnAST);
			equalityExpression_AST = (AST) currentAST.root;
		} else if ((LA(1) == IDENT) && (LA(2) == IN)) {
			inExpression();
			astFactory.addASTChild(currentAST, returnAST);
			equalityExpression_AST = (AST) currentAST.root;
		} else {
			throw new NoViableAltException(LT(1), getFilename());
		}

		returnAST = equalityExpression_AST;
	}

	public final void logicalOrExpression() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalOrExpression_AST = null;

		logicalAndExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
			_loop72: do {
				if ((LA(1) == OR)) {
					AST tmp81_AST = null;
					tmp81_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp81_AST);
					match(OR);
					logicalAndExpression();
					astFactory.addASTChild(currentAST, returnAST);
				} else {
					break _loop72;
				}

			} while (true);
		}
		logicalOrExpression_AST = (AST) currentAST.root;
		returnAST = logicalOrExpression_AST;
	}

	public final void logicalAndExpression() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalAndExpression_AST = null;

		negatedExpression();
		astFactory.addASTChild(currentAST, returnAST);
		{
			_loop75: do {
				if ((LA(1) == AND)) {
					AST tmp82_AST = null;
					tmp82_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp82_AST);
					match(AND);
					negatedExpression();
					astFactory.addASTChild(currentAST, returnAST);
				} else {
					break _loop75;
				}

			} while (true);
		}
		logicalAndExpression_AST = (AST) currentAST.root;
		returnAST = logicalAndExpression_AST;
	}

	public final void negatedExpression() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST negatedExpression_AST = null;

		equalityExpression();
		astFactory.addASTChild(currentAST, returnAST);
		negatedExpression_AST = (AST) currentAST.root;
		returnAST = negatedExpression_AST;
	}

	public final void equalsToExpression() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST equalsToExpression_AST = null;

		AST tmp83_AST = null;
		tmp83_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp83_AST);
		match(IDENT);
		AST tmp84_AST = null;
		tmp84_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp84_AST);
		match(EQ);
		constant();
		astFactory.addASTChild(currentAST, returnAST);
		equalsToExpression_AST = (AST) currentAST.root;
		returnAST = equalsToExpression_AST;
	}

	public final void inExpression() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST inExpression_AST = null;

		AST tmp85_AST = null;
		tmp85_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp85_AST);
		match(IDENT);
		AST tmp86_AST = null;
		tmp86_AST = astFactory.create(LT(1));
		astFactory.makeASTRoot(currentAST, tmp86_AST);
		match(IN);
		match(OPEN);
		variable();
		astFactory.addASTChild(currentAST, returnAST);
		{
			_loop81: do {
				if ((LA(1) == COMMA)) {
					match(COMMA);
					variable();
					astFactory.addASTChild(currentAST, returnAST);
				} else {
					break _loop81;
				}

			} while (true);
		}
		match(CLOSE);
		inExpression_AST = (AST) currentAST.root;
		returnAST = inExpression_AST;
	}

	public final void constant() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constant_AST = null;

		switch (LA(1)) {
		case IDENT: {
			column();
			astFactory.addASTChild(currentAST, returnAST);
			constant_AST = (AST) currentAST.root;
			break;
		}
		case NUMERICAL:
		case QUOTED_STRING:
		case PARAM: {
			variable();
			astFactory.addASTChild(currentAST, returnAST);
			constant_AST = (AST) currentAST.root;
			break;
		}
		default: {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		returnAST = constant_AST;
	}

	public final void column() throws RecognitionException, TokenStreamException {

		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST column_AST = null;

		AST tmp90_AST = null;
		tmp90_AST = astFactory.create(LT(1));
		astFactory.addASTChild(currentAST, tmp90_AST);
		match(IDENT);
		column_AST = (AST) currentAST.root;
		returnAST = column_AST;
	}

	public static final String[] _tokenNames = { "<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "\"and\"", "\"as\"",
			"\"delete\"", "\"from\"", "\"in\"", "\"insert\"", "\"into\"", "\"values\"", "\"value\"", "\"max\"",
			"\"min\"", "\"avg\"", "\"sum\"", "\"count\"", "\"or\"", "\"select\"", "\"set\"", "\"update\"", "\"where\"",
			"\"left\"", "\"right\"", "\"inner\"", "\"cross\"", "\"outer\"", "\"join\"", "\"on\"", "\"limit\"",
			"\"order\"", "\"by\"", "\"asc\"", "\"desc\"", "\"hint\"", "\"force_read\"", "SELECT_ROOT", "INSERT_ROOT",
			"DELETE_ROOT", "UPDATE_ROOT", "COLUMN_LIST", "OPEN_COMMENT", "CLOSE_COMMENT", "STAR", "COMMA", "IDENT",
			"OPEN", "CLOSE", "NUMERICAL", "EQ", "QUOTED_STRING", "PARAM", "LT", "GT", "SQL_NE", "NE", "LE", "GE",
			"OPEN_BRACKET", "CLOSE_BRACKET", "CONCAT", "PLUS", "MINUS", "DIV", "MOD", "COLON", "DOT",
			"ID_START_LETTER", "ID_LETTER", "ESCqs", "WS" };

	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap = null;
	};

	private static final long[] mk_tokenSet_0() {
		long[] data = { 35184699244544L, 0L };
		return data;
	}

	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());

}
