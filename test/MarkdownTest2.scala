import com.tristanhunt.knockoff._
object MarkdownTest2 {
def main(args: Array[String]) {
    CommandList.init()
    //       sssss
    //       
    //       $test
    //       $test:test
    //       samples
    //       
    //       $test:test=test
    //       $test:test=test,test2=test
    //       $test:test=test,test2=test
    //	   $end
    //       
    //$test
    //       
    //       
    //       

    var markdown = new RakuzaDiscounter().knockoff("""
- begin t2-
 -- begin t3--

-- end--        
- end-

--begin t4--
        --begin nested test=value test2=value2--
        nested kommand
        
$command
 $command2 test=value samples 
  $command3 test=value samples 
 
--end--
        
-end- 
#コメント コメント コメント
        
test.sample [Samples]($changePage:to=xxxx,key=value,key=value) samples

        
        """.trim());

    println("----------")
    markdown.foreach { m =>
      println(m)
    }
    
    var writer = new RakuzaXHTMLWriter();
//    var html = writer.toXML(markdown)
//    
//    println(html.toString())
    //   val name ="sample"
    //   var xml = <Service id={name}></Service>
    //   println(xml)
  }
}