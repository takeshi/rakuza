import com.tristanhunt.knockoff.DefaultDiscounter._
import scala.util.parsing.input.CharSequenceReader
import com.tristanhunt.knockoff._
import scala.collection.mutable.ListBuffer
import scala.util.parsing.input._
import java.util.LinkedList

class MarkDown extends Discounter {
}

object MarkDown {
  def main(args: Array[String]) {
    CommandList.init()
//    - begin controlgroup type=horizontal -
//[next](#inline-button:icon=delete,mini)
//[next](#inline-button:icon=delete,mini)
//[next](#inline-button:icon=delete,mini)
//
// <http://example.com/>
//
//#コメント　超えんと コメント
//
//-- begin link:to=http://google.com --
//$img src=http://www.google.com/images/logo.gif
//-- end --
//        
//- end -
//        
    var markdown = new RakuzaDiscounter().knockoff("""
[next](:inline-button icon=delete mini)<!-- コメント -->
        
#page
[sample2](:title)
#head theme=a
[sample3]:title
#end <!-- comment -->
[sample4]:title
#end 
        """.trim());

    println("----------")
    markdown.foreach { m =>
      println(m)
    }
    
//    var writer = new RakuzaXHTMLWriter();
//    var html = writer.toXML(markdown)
//    
//    println(html.toString())
  }
}