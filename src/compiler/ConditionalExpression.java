package compiler;

public class ConditionalExpression extends Expression{
    public Expression body;

    public Expression condition;

    public Expression elseBody;

    public ConditionalExpression(int row, int col,
                                 Expression body, Expression condition, Expression elseBody) {
        super(row, col);
        this.body = body;
        this.condition = condition;
        if (elseBody != null) {
            this.elseBody = elseBody;
        }
    }
}
