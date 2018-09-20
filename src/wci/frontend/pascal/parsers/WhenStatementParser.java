package wci.frontend.pascal.parsers;

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
	
	// Synchronization set for WHEN.
    private static final EnumSet<PascalTokenType> THEN_SET =
        StatementParser.STMT_START_SET.clone();
    static {
        THEN_SET.add(THEN);
        THEN_SET.addAll(StatementParser.STMT_FOLLOW_SET);
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
			token = nextToken();
			ICodeNode statementNode = null;
	        switch ((PascalTokenType) token.getType()) {

	        	case BEGIN: {
	        		CompoundStatementParser compoundParser =
	        				new CompoundStatementParser(this);
	        		statementNode = compoundParser.parse(token);
	        		break;
	        		}
	        	case IDENTIFIER: {
	        			AssignmentStatementParser assignmentParser =
	        				new AssignmentStatementParser(this);
	        			statementNode = assignmentParser.parse(token);
	        			break;
	        		}
	            default: {
	                statementNode = ICodeFactory.createICodeNode(NO_OP);
	                break;
	            }
	        }
			return statementNode;
		}

        // Create an IF node.
        ICodeNode ifNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.IF);

        // Parse the expression.
        // The IF node adopts the expression subtree as its first child.
        ExpressionParser expressionParser = new ExpressionParser(this);
        ifNode.addChild(expressionParser.parse(token));
        
        // Check if current token is the Special symbol "=>"
        token = currentToken();
        if(token.getType() == PROCESS_ARROW) {
        	token = nextToken();
        	//TODO: Synchornize if NOT the Special arrow.
        }
        
        ICodeNode statementNode = null;
        switch ((PascalTokenType) token.getType()) {

        	case BEGIN: {
        		CompoundStatementParser compoundParser =
        				new CompoundStatementParser(this);
        		statementNode = compoundParser.parse(token);
        		break;
        		}
        	case IDENTIFIER: {
        			AssignmentStatementParser assignmentParser =
        				new AssignmentStatementParser(this);
        			statementNode = assignmentParser.parse(token);
        			break;
        		}
            default: {
                statementNode = ICodeFactory.createICodeNode(NO_OP);
                break;
            }
        }
        // TODO: Synchronize if not A statement correct
        
        ifNode.addChild(statementNode);
        
     // Check if current token is the Special symbol ";"
        token = currentToken();
        if(token.getType() == SEMICOLON) {
        	token = nextToken();
        	//TODO: Synchornize if NOT the semicolon
        }
        
        // add the recursive cascading IF as a ELSE node.
        ifNode.addChild(parseCascadingIf());
        
		return ifNode; //TODO: returns IF node.
        
	}

}
