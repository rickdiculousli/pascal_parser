package wci.frontend.pascal.parsers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.pascal.*;
import wci.intermediate.*;
import wci.intermediate.icodeimpl.*;

import static wci.frontend.pascal.PascalTokenType.*;
import static wci.frontend.pascal.PascalErrorCode.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

public class WhenStatementParser extends StatementParser{
	
	// Synchronization set for special arrow.
    private static final EnumSet<PascalTokenType> ARROW_SET =
        EnumSet.of(PROCESS_ARROW, SEMICOLON);
    private static final EnumSet<PascalTokenType> FOLLOW_SET =
    		EnumSet.of(OTHERWISE);
    static {
    	FOLLOW_SET.addAll(ExpressionParser.EXPR_START_SET); //Find start of next statement series.
    	FOLLOW_SET.addAll(StatementParser.STMT_FOLLOW_SET);
    	ARROW_SET.addAll(StatementParser.STMT_START_SET);
    }

	public WhenStatementParser(PascalParserTD parent) 
	{
		super(parent);
	}
	
	//TODO: Enum set for synchronization?
	
	//TODO: Create When logic
	public ICodeNode parse(Token token) throws Exception
	{
		token = nextToken(); //Consume WHEN
		
		return parseCascadingIf();
        
	}
	
	//recursive call to get cascading IF's
	public ICodeNode parseCascadingIf() throws Exception {
		
		Token token = currentToken();
		// Base Case, Return Otherwise statement
		if(token.getType() == OTHERWISE)
		{
			 nextToken();
			 token = synchronize(ARROW_SET);
		        if(token.getType() == PROCESS_ARROW) {
		        	token = nextToken();
		        }else {
		        	 errorHandler.flag(token, MISSING_ARROW, this);
		        }
			ICodeNode statementNode = null;
	        
			if((PascalTokenType) token.getType() == IDENTIFIER ||(PascalTokenType) token.getType() == BEGIN) {
	        	StatementParser statementParser = new StatementParser(this);
	        	statementNode = statementParser.parse(token);
	        }
	        else {
	        	 statementNode = ICodeFactory.createICodeNode(NO_OP);
	        	 errorHandler.flag(token, INVALID_STATEMENT, this);
	        }
	        
	        token = synchronize(FOLLOW_SET);
	        if(token.getType() != END)
	        {
	        	errorHandler.flag(token, MISSING_END, this);
	        }else {
	        	token = nextToken();
	        }
//	        if(token.getType() != SEMICOLON) {
//	        	errorHandler.flag(token, MISSING_SEMICOLON, this);
//	        }else {
//	        	nextToken();
//	        }
	        return statementNode;
		}

        // Create an IF node.
        ICodeNode ifNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.IF);

        // Parse the expression.
        // The IF node adopts the expression subtree as its first child.
        ExpressionParser expressionParser = new ExpressionParser(this);
        ifNode.addChild(expressionParser.parse(token));
        
        // Check if current token is the Special symbol "=>"
        token = synchronize(ARROW_SET);
        if(token.getType() == PROCESS_ARROW) {
        	token = nextToken();
        }else {
        	 errorHandler.flag(token, MISSING_ARROW, this);
        }
        
        ICodeNode statementNode = null;
        
        if((PascalTokenType) token.getType() == IDENTIFIER ||(PascalTokenType) token.getType() == BEGIN) {
        	StatementParser statementParser = new StatementParser(this);
        	statementNode = statementParser.parse(token);
        }
        else {
        	 statementNode = ICodeFactory.createICodeNode(NO_OP);
        	 errorHandler.flag(token, INVALID_STATEMENT, this);
        }
        
        ifNode.addChild(statementNode);
        
     // Check if current token is the Special symbol ";"
        token = synchronize(FOLLOW_SET);
        if(token.getType() == SEMICOLON) {
        	token = nextToken();
        }else {
        	 errorHandler.flag(token, MISSING_SEMICOLON, this);
        }
        
        // add the recursive cascading IF as a ELSE node.
        if(token.getType() != END) {
        	 ifNode.addChild(parseCascadingIf());
        }else {
        	errorHandler.flag(token, MISSING_OTHERWISE, this);
        	nextToken();
        }
       
        
		return ifNode; //TODO: returns IF node.
        
	}

}
