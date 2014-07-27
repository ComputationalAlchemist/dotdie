package skaianet.die.standalone;

import skaianet.die.ast.Statement;
import skaianet.die.back.EnergyPacket;
import skaianet.die.back.ExecutionContext;
import skaianet.die.front.Parser;
import skaianet.die.front.ParsingException;
import skaianet.die.middle.CompiledProcedure;
import skaianet.die.middle.Compiler;
import skaianet.die.middle.CompilingException;

import java.io.FileReader;
import java.io.IOException;

/**
 * Created on 2014-07-23.
 */
public class Standalone {

    public static void main(String[] args) throws IOException, ParsingException, CompilingException, InterruptedException {
        String filename = "src/ack.~ath";
        Parser parser = new Parser(filename, new FileReader(filename));
        System.out.println("=== Source Code ===");
        parser.dumpCode();
        Statement stmt = parser.parseProgram();
        System.out.println("=== Abstract Syntax Tree ===");
        stmt.print();
        Compiler compiler = new Compiler();
        CompiledProcedure procedure = compiler.compile(stmt, new String[0]);
        System.out.println("=== Compiled Bytecode ===");
        procedure.print(0);
        ExecutionContext context = new ExecutionContext(new StandaloneExtension());
        context.init(procedure, new EnergyPacket(0, null));
        System.out.println("=== Execution ===");
        //context.report();
        long begin = System.currentTimeMillis();
        while (context.runSweep()) {
            //Thread.sleep(10);
        }
        long end = System.currentTimeMillis();
        System.out.println("=== Timing Statistics ===");
        System.out.println((end - begin) / 1000.0 + " seconds.");
        System.out.println("=== Printout End ===");
    }
}
