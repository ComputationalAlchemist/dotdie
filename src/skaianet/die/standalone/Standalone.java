package skaianet.die.standalone;

import skaianet.die.ast.Statement;
import skaianet.die.back.EnergyPacket;
import skaianet.die.back.ExecutionContext;
import skaianet.die.front.Parser;
import skaianet.die.front.ParsingException;
import skaianet.die.middle.CompiledProcedure;
import skaianet.die.middle.Compiler;
import skaianet.die.middle.CompilingException;
import skaianet.die.utils.Utilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * Created on 2014-07-23.
 */
public class Standalone {

    public static PrintStream standaloneOutput;
    public static InputStream standaloneInput;

    public static void main(String[] args) throws IOException, ParsingException, CompilingException {
        String filename = "src/ack.~ath";
        execute(filename, true, System.out, System.in);
    }

    public static void execute(String filename, boolean useTiming, PrintStream out, InputStream in) throws IOException, ParsingException, CompilingException {
        standaloneOutput = out;
        standaloneInput = in;
        Parser parser = new Parser(filename, Utilities.readEntireFile(filename));
        out.println("=== Source Code ===");
        parser.dumpCode(out);
        Statement stmt = parser.parseProgram();
        out.println("=== Abstract Syntax Tree ===");
        stmt.print(out);
        Compiler compiler = new Compiler();
        CompiledProcedure procedure = compiler.compile(stmt, new String[0]);
        out.println("=== Compiled Bytecode ===");
        procedure.print(0, out);
        ExecutionContext context = new ExecutionContext(new StandaloneExtension());
        context.init(procedure, new EnergyPacket(0, null));
        out.println("=== Execution ===");
        long begin = useTiming ? System.currentTimeMillis() : 0;
        while (context.runSweep()) ;
        if (useTiming) {
            long end = System.currentTimeMillis();
            out.println("=== Timing Statistics ===");
            out.println((end - begin) / 1000.0 + " seconds.");
        }
        out.println("=== Printout End ===");
    }
}
