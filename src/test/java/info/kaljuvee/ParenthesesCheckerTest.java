package info.kaljuvee;

import org.junit.Assert;
import org.junit.Test;

public class ParenthesesCheckerTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNullInput() {
        ParenthesesChecker pc = new ParenthesesChecker(null);
    }

    @Test
    public void testEmptyInput() {
        ParenthesesChecker pc = new ParenthesesChecker("");
        Assert.assertEquals(true, pc.isBalanced());
    }

    @Test
    public void testRandomExpression() {
        ParenthesesChecker pc = new ParenthesesChecker("sdflj$*adlk");
        Assert.assertEquals(true, pc.isBalanced());
    }

    @Test
    public void testBasicBalanced() {
        ParenthesesChecker pc = new ParenthesesChecker("((x)(x))");
        Assert.assertEquals(true, pc.isBalanced());
    }

    @Test
    public void testComplexBalanced() {
        ParenthesesChecker pc = new ParenthesesChecker("((x)x*y*((x)())(...))");
        Assert.assertEquals(true, pc.isBalanced());
    }

    @Test
    public void testStartingClose() {
        ParenthesesChecker pc = new ParenthesesChecker("a*)(foo)(bar)((x))");
        Assert.assertEquals(false, pc.isBalanced());
    }

    @Test
    public void testNonClosing() {
        ParenthesesChecker pc = new ParenthesesChecker("(bar(foo)*(bar)");
        Assert.assertEquals(false, pc.isBalanced());
    }

    @Test
    public void testExtraStart() {
        ParenthesesChecker pc = new ParenthesesChecker("((bar(foo)*(bar))");
        Assert.assertEquals(false, pc.isBalanced());
    }
}
