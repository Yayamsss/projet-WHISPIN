/*
    Auteur : Ali GÜRKAN
*/

import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class GraphicHelper 
{
    public static Color makeColorWithOpacity(Color color, float opacity)
    {
        if (opacity<0 || 1.0<opacity)
            throw new IllegalArgumentException("Opacity must be between 0 and 1\n");
        return Color.color(
            color.getRed(),
            color.getGreen(),
            color.getBlue(),
            (double) opacity
        );
    }

    public static boolean addOpacityWithClamp(GraphicShape shape, float opacityToAdd)
    {
        shape.setOpacity(Math.clamp(shape.getOpacity() + opacityToAdd, 0.0f, 1.0f));

        return shape.getOpacity() == 0.0f || shape.getOpacity() == 1.0f;
    }

    public static GraphicRectangle createRectangleObj(float width, float height, float borderRadius, Color color, int layer)
    {
        GameObject obj = GameLoop.getInstance().addObject(GameObject.class);
        GraphicRectangle rectangle = obj.setGraphicShape(GraphicRectangle.class);

        rectangle.init(width, height, obj, color, layer, borderRadius);
        return rectangle;
    }

    public static GraphicOval createOvalObj(float width, float height, Color color, int layer)
    {
        GameObject obj = GameLoop.getInstance().addObject(GameObject.class);
        GraphicOval oval = obj.setGraphicShape(GraphicOval.class);

        oval.init(width, height, obj, color, layer);
        return oval;
    }

    public static GraphicLine createLineObj(float length, float width, float rotation, Color color, int layer)
    {
        GameObject obj = GameLoop.getInstance().addObject(GameObject.class);
        GraphicLine line = obj.setGraphicShape(GraphicLine.class);

        line.init(obj, length, width, rotation, color, layer);
        return line;
    }

    public static GraphicImage createImageObj(float width, float height, Image texture, int layer)
    {
        GameObject obj = GameLoop.getInstance().addObject(GameObject.class);
        GraphicImage image = obj.setGraphicShape(GraphicImage.class);

        image.init(texture, width, height, obj, layer);
        return image;
    }

    public static GraphicButton createButton(String _text, float x, float y)
    {
        return createButton(GraphicButton.class, _text, x, y, -1, -1);
    }

    public static GraphicButton createButton(String _text, float x, float y, ButtonCallBack callBack)
    {
        GraphicButton newButton = createButton(GraphicButton.class, _text, x, y, -1, -1);
        newButton.setCallBack(callBack);

        return newButton;
    }

    public static GraphicButton createButton(String _text, float x, float y, float width, float height, ButtonCallBack callBack)
    {
        GraphicButton newButton = createButton(GraphicButton.class, _text, x, y, width, height);
        newButton.setCallBack(callBack);

        return newButton;
    }

    public static <B extends GraphicButton> B createButton(Class<B> buttonClass, String _text, float x, float y)
    {
        return createButton(buttonClass, _text, x, y, -1, -1);
    }
    
    public static <B extends GraphicButton> B createButton(Class<B> buttonClass, String _text, float x, float y, float _width, float _height)
    {
        B button = GameLoop.getInstance().addObject(buttonClass);

        button.setPosition(new Vector2(x,y));
        button.setText(_text);

        if (_width != -1)
        {
            button.setWidth(_width);
        }

        if (_height!=-1)
        {
            button.setHeight(_height);
        }

        button.setHidden(true);
        return button;
    }

    public static GraphicHeart[] createHearts(int heartCount)
    {
        GraphicHeart hearts[] = new GraphicHeart[heartCount];

        float positionX = GraphicsModule.getWitdh() / 1.225f;
        float positionY = GraphicsModule.getHeight() / 10.0f;

        for (int i = 0 ; i < heartCount; i++)
        {
            hearts[i] = GameLoop.getInstance().addObject(GraphicHeart.class);

            hearts[i].setPosition(new Vector2(positionX, positionY));
            hearts[i].setRank(heartCount - i - 1);

            positionX += 100.0f;
        }

        return hearts;
    }

    public static ArrayList<GameObject> createStarSky(int numStars)
    {
        ArrayList<GameObject> starList = new ArrayList<>();

        for (int i = 0; i < numStars; i++)
        {
            starList.add(GameLoop.getInstance().addObject(ElementStar.class));
        }

        return starList;
    }

    public static ArrayList<GameObject> createStarSky(int numStars, Vector2 spawnRangeX, Vector2 spawnRangeY)
    {
        ArrayList<GameObject> starList = new ArrayList<>();

        for (int i = 0; i < numStars; i++)
        {
            ElementStar star = GameLoop.getInstance().addObject(ElementStar.class);

            star.setSpawnRangeX(spawnRangeX);
            star.setSpawnRangeY(spawnRangeY);

            starList.add(star);
        }

        return starList;
    }

    public static ArrayList<GameObject> createBuildings(float offsetY, float gap)
    {
        ArrayList<GameObject> buildings = new ArrayList<>();
        float offsetX = 0.0f;

        while (offsetX < GraphicsModule.getWitdh()) 
        {
            ElementBuilding newBuilding = GameLoop.getInstance().addObject(ElementBuilding.class);

            newBuilding.setPosition(new Vector2(
                offsetX + newBuilding.getBuilding().getWitdh() / 2,
                offsetY - newBuilding.getBuilding().getHeight() / 2));
            offsetX += newBuilding.getBuilding().getWitdh() + 10;

            buildings.add(newBuilding);
        }

        return buildings;
    }

    public static ArrayList<GameObject> createRain(int numRain)
    {
        ArrayList<GameObject> rainList = new ArrayList<>();

        for (int i = 0; i<numRain; i++)
        {
            rainList.add(GameLoop.getInstance().addObject(ElementRain.class));
        }

        return rainList;
    }

    public static ArrayList<GameObject> createCars()
    {
        ElementCar rightCar = GameLoop.getInstance().addObject(ElementCar.class);
        ElementCar leftcar = GameLoop.getInstance().addObject(ElementCar.class);

        rightCar.setDirection(ElementCar.Direction.toRight);
        leftcar.setDirection(ElementCar.Direction.toLeft);

        ArrayList<GameObject> carList = new ArrayList<>(2);

        carList.add(rightCar);
        carList.add(leftcar);

        return carList;
    }

    public static ArrayList<GameObject> createRoad(float gap, float offsetY)
    {
        ArrayList<GameObject> lineList = new ArrayList<>();
        float offsetX = gap;

        while (offsetX < GraphicsModule.getWitdh()) 
        {
            GameObject lineRoadObj = GameLoop.getInstance().addObject(GameObject.class);
            GraphicLine lineRoadShape = lineRoadObj.setGraphicShape(GraphicLine.class);

            lineRoadObj.setPosition(new Vector2(
                offsetX,
                (GraphicsModule.getHeight() + offsetY) / 2));
            lineRoadShape.init(lineRoadObj,26, 4, 0, Color.ORANGE, 0);

            offsetX += gap;
            lineList.add(lineRoadObj);
        }

        return lineList;
    }

    public static ArrayList<GameObject> generateLineBackground(int color)
    {
        ArrayList<GameObject> lineList = new ArrayList<>();
        for (int i = 0; i< 36; i++)
        {
            GameObject lineObj = GameLoop.getInstance().addObject(GameObject.class);
            lineObj.setPosition(new Vector2(
                GraphicsModule.getWitdh() / 2,
                GraphicsModule.getHeight() / 2
            ));
            
            GraphicLine line = lineObj.setGraphicShape(GraphicLine.class);
            line.init(
                lineObj, 
                1920, 
                1.5f, 
                5*i + 3, 
                GraphicHelper.makeColorWithOpacity(
                    GraphicHelper.makeColorFromHex(color),
                    1
                ), 
                30
            );

            lineList.add(lineObj);
        }

        return lineList;
    }

    public static ArrayList<GameObject> generateOvalBackground(int[] color)
    {
        ArrayList<GameObject> ovalList = new ArrayList<>();

        GameObject ovalObj0 = GameLoop.getInstance().addObject(GameObject.class);
        ovalObj0.setPosition(new Vector2(
            GraphicsModule.getWitdh() / 2,
            GraphicsModule.getHeight() / 2
        ));

        GameObject ovalObj1 = GameLoop.getInstance().addObject(GameObject.class);
        ovalObj1.setPosition(ovalObj0.getPosition());

        GameObject ovalObj2 = GameLoop.getInstance().addObject(GameObject.class);
        ovalObj2.setPosition(ovalObj0.getPosition());

        GameObject ovalObj3 = GameLoop.getInstance().addObject(GameObject.class);
        ovalObj3.setPosition(ovalObj0.getPosition());

        GraphicOval oval0 = ovalObj0.setGraphicShape(GraphicOval.class);
        oval0.init(
            GraphicsModule.getWitdh(), 
            GraphicsModule.getHeight(), 
            ovalObj0, 
            GraphicHelper.makeColorFromHex(color[0]), 
            31
        );
        oval0.setOpacity(0.1f);

        GraphicOval oval1 = ovalObj1.setGraphicShape(GraphicOval.class);
        oval1.init(
            GraphicsModule.getWitdh() - 300, 
            GraphicsModule.getHeight() - 250, 
            ovalObj1, 
            GraphicHelper.makeColorFromHex(color[1]), 
            32
        );
        oval1.setOpacity(0.3f);

        GraphicOval oval2 = ovalObj2.setGraphicShape(GraphicOval.class);
        oval2.init(
            GraphicsModule.getWitdh() - 500, 
            GraphicsModule.getHeight() - 400, 
            ovalObj2, 
            GraphicHelper.makeColorFromHex(color[2]), 
            33
        );
        oval2.setOpacity(0.3f);

        GraphicOval oval3 = ovalObj3.setGraphicShape(GraphicOval.class);
        oval3.init(
            GraphicsModule.getWitdh() - 1200, 
            GraphicsModule.getHeight() - 525, 
            ovalObj3, 
            GraphicHelper.makeColorFromHex(color[3]), 
            34
        );
        oval3.setOpacity(0.4f);

        ovalList.addAll(List.of(ovalObj0, ovalObj1, ovalObj2, ovalObj3));
        return ovalList;
    }

    public static Image loadImage(String pathImage)
    {
        return new Image(pathImage, true);
    }

    public static Color makeColorFromHex(int hexadecimale)
    {
        return Color.rgb(
            (hexadecimale & 0xFF0000) >> 16,
            (hexadecimale & 0xFF00) >> 8, 
            hexadecimale & 0xFF);
    }
}
