package skaianet.die.back;

public class LoopContext {
    private final Object object;
    private final ExecutionContext ctx;
    private double baseEnergy;

    public LoopContext(Object object, ExecutionContext ctx) {
        this.object = object;
        this.ctx = ctx;
    }

    public boolean setupLoop() {
        baseEnergy = ctx.calcEnergy(object);
        return ctx.isAlive(object);
    }

    public boolean continueLoop() {
        return ctx.isAlive(object);
    }

    public Object extractEnergy(EnergyPacket parent) {
        double newEnergy = ctx.calcEnergy(object), oldEnergy = baseEnergy;
        baseEnergy = newEnergy;
        return new EnergyPacket(newEnergy - oldEnergy, parent);
    }
}
