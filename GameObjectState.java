/*
    Auteur: Ryane Menaï
*/

public abstract class GameObjectState <T extends GameObjectEx>
{
    public void execute(T obj) {}
    public void executeLate(T obj) {}
}