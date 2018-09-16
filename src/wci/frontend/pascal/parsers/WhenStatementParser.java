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
	public ICodeNode parse(Token token)
	{
		token = nextToken(); //Consume WHEN
		
		// Create an IF node.
        ICodeNode ifNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.IF);
        
	}
	
	public void parseCascadingIf(Token token) {
		token = nextToken(); //Consume WHEN
		
		// Create an IF node.
        ICodeNode ifNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.IF);
        
	}

}
