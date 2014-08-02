package skaianet.die.standalone;

import skaianet.die.ast.Statement;
import skaianet.die.back.EnergyPacket;
import skaianet.die.back.ExecutionWrapper;
import skaianet.die.front.ColoredIdentifier;
import skaianet.die.front.Parser;
import skaianet.die.front.ParsingException;
import skaianet.die.middle.CompiledProcedure;
import skaianet.die.middle.Compiler;
import skaianet.die.middle.CompilingException;
import skaianet.die.utils.Utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class Standalone {

    public static PrintStream standaloneOutput;
    public static InputStream standaloneInput;
    public static File resourceDir = null;

    public static void main(String[] args) {
        String filename = "src/skaianet/die/examples/pesterchum.~ath";
        resourceDir = new File(filename).getParentFile();
        try {
            try {
                execute(filename, true, System.out, System.in);
            } finally {
                System.out.flush();
            }
            System.exit(0);
        } catch (Throwable thr) {
            thr.printStackTrace();
            System.exit(1);
        }
    }

    public static void execute(String filename, boolean useTiming, PrintStream out, InputStream in) throws IOException, ParsingException, CompilingException, InterruptedException {
        standaloneOutput = out;
        standaloneInput = in;
        Parser parser = new Parser(filename, Utilities.readEntireFile(filename));
        out.println("=== Source Code ===");
        parser.dumpCode(out);
        Statement stmt = parser.parseProgram();
        out.println("=== Abstract Syntax Tree ===");
        stmt.print(out);
        Compiler compiler = new Compiler();
        CompiledProcedure procedure = compiler.compile(stmt, new ColoredIdentifier[0], null);
        out.println("=== Compiled Bytecode ===");
        procedure.print(0, out);
        ExecutionWrapper wrapper = new ExecutionWrapper(new StandaloneExtension(), procedure, new EnergyPacket(0, null));
        out.println("=== Execution ===");
        long begin = useTiming ? System.currentTimeMillis() : 0;
        while (true) {
            long deadline = wrapper.continueExecution();
            if (deadline < 0) { // done
                break;
            } else if (deadline == 0) { // ready immediately
                // fall through
            } else {
                long delay = deadline - System.currentTimeMillis();
                if (delay > 0) {
                    Thread.sleep(delay);
                }
            }
            //wrapper.report(System.out);
        }
        if (useTiming) {
            long end = System.currentTimeMillis();
            out.println("=== Timing Statistics ===");
            out.println((end - begin) / 1000.0 + " seconds.");
        }
        out.println("=== Printout End ===");
    }
}
