package main;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.threed.jpct.SimpleVector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

/**
 * Created by spx on 20.06.15.
 */
public class WorldLoader {

    static Document getDomFromXmlString(String xmlString) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document document;
        builder = factory.newDocumentBuilder();
        document = builder.parse( new InputSource( new StringReader( xmlString ) ) );
        return document;
    }

    static void refillWorldWithString(GameWorld world, String worldDescription)
    {
        //wyczysc swiat przed dodaniem nowych obiektow
        world.clearActiveObjects();
        System.gc();

        //roboczo bedzie caly czas wypelnial tym samym bez wzgledu na dane wejsciowe,
        //tj nie analizuje stringa podanego na wejsciu

        /* * /
        world.staticObjects.add(PhysicsFactory.getStaticBox("static", new SimpleVector(30, 5, 30), new SimpleVector(0, 0, 0), new SimpleVector(0, 0, 0), world, 1.0f));
        ActiveGameObject rotational = (PhysicsFactory.getBox("rotational", new SimpleVector(30,5,30), new SimpleVector(40,0,0), new SimpleVector(0,0,0), world, 150, 0.0f, 0.0f, 0, 0 ));
        rotational.object.setAdditionalColor(0, 0, 255);
        rotational.rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
        world.rotationalObjects.add(rotational);

        world.hero = PhysicsFactory.getBox("hero", new SimpleVector(6,12,6), new SimpleVector(0, -10, 0), new SimpleVector(0,0,0), world, 18, 1.1f, 0.2f, 0, 0);
        world.hero.object.setAdditionalColor(100, 100, 0);
        world.hero.rigidBody.setAngularFactor(0.0f);
        world.hero.rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
        /* */

        /* */
        try
        {
            Document xmlDomDocument = getDomFromXmlString(worldDescription);
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile("level/*");
            NodeList worldElements = (NodeList) expr.evaluate(xmlDomDocument, XPathConstants.NODESET);

            for (int i = 0; i < worldElements.getLength(); i++)
            {
                Element node = (Element)worldElements.item(i);
                float x = Float.parseFloat(node.getAttribute("x"));
                float y = Float.parseFloat(node.getAttribute("y"));
                float z = Float.parseFloat(node.getAttribute("z"));
                SimpleVector position = new SimpleVector(x, y, z);
                if (node.getNodeName().equals("hero"))
                {
                    ActiveGameObject hero = AssetsFactory.getHero(world, position);
                    world.hero = hero;
                }
                else if (node.getNodeName().equals("static"))
                {
                    float sx = Float.parseFloat(node.getAttribute("sx"));
                    float sy = Float.parseFloat(node.getAttribute("sy"));
                    float sz = Float.parseFloat(node.getAttribute("sz"));

                    float rx = Float.parseFloat(node.getAttribute("rx"));
                    float ry = Float.parseFloat(node.getAttribute("ry"));
                    float rz = Float.parseFloat(node.getAttribute("rz"));
                    SimpleVector scale = new SimpleVector(sx, sy, sz);
                    SimpleVector rotation = new SimpleVector(rx, ry, rz);
                    ActiveGameObject staticBlock = AssetsFactory.getStatic(world,
                            position, rotation, scale);
                    world.staticObjects.add(staticBlock);
                }
                else if (node.getNodeName().equals("rotational"))
                {
                    float sx = Float.parseFloat(node.getAttribute("sx"));
                    float sy = Float.parseFloat(node.getAttribute("sy"));
                    float sz = Float.parseFloat(node.getAttribute("sz"));

                    float rx = Float.parseFloat(node.getAttribute("rx"));
                    float ry = Float.parseFloat(node.getAttribute("ry"));
                    float rz = Float.parseFloat(node.getAttribute("rz"));

                    float mass = Float.parseFloat(node.getAttribute("mass")) * 10;
                    SimpleVector scale = new SimpleVector(sx, sy, sz);
                    SimpleVector rotation = new SimpleVector(rx, ry, rz);
                    ActiveGameObject rotationalBlock = AssetsFactory.getRotational(world,
                            position, rotation, scale, mass);
                    world.rotationalObjects.add(rotationalBlock);
                }
            }
        }
        catch (Exception e)
        {
            //tu jezeli wystapil blad parsowania pliku z opisem swiata, to zrobic obsluge bledow
            e.printStackTrace();
        }
        /* */


    }
}
