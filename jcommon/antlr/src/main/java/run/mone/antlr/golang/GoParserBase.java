package run.mone.antlr.golang;

import java.util.List;
import org.antlr.v4.runtime.*;

/**
 * @author goodjava@qq.com
 * @date 2024/1/29 16:33
 */
public abstract class GoParserBase extends Parser
{
    protected GoParserBase(TokenStream input) {
        super(input);
    }


    /**
     * Returns true if the current Token is a closing bracket (")" or "}")
     */
    protected boolean closingBracket()
    {
        BufferedTokenStream stream = (BufferedTokenStream)_input;
        int prevTokenType = stream.LA(1);

        return prevTokenType == GoParser.R_CURLY || prevTokenType == GoParser.R_PAREN;
    }
}
