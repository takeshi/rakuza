package com.tristanhunt.knockoff

import com.tristanhunt.knockoff.DefaultDiscounter._
import scala.util.parsing.input.CharSequenceReader
import com.tristanhunt.knockoff._
import scala.collection.mutable.ListBuffer
import scala.util.parsing.input._
import java.util.LinkedList

class RakuzaDiscounter extends Discounter {

  override def log(msg: String) {
//    println(msg)
  }

  override def createChunkStream(reader: Reader[Char]): Stream[(Chunk, Position)] = {
    val chunkParser = new RakuzaParser

    if (reader.atEnd) return Stream.empty
    chunkParser.parse(chunkParser.chunk, reader) match {
      case chunkParser.Error(msg, next) => {
        log(msg)
        log("next == reader : " + (next == reader))
        createChunkStream(next)
        throw new Exception(msg)
      }
      case chunkParser.Failure(msg, next) => {
        log(msg)
        log("next == reader : " + (next == reader))
        createChunkStream(next)
        throw new Exception(msg)
      }
      case chunkParser.Success(result, next) => {
        log("-- result " + result.toString)
        Stream.cons((result, reader.pos), createChunkStream(next))
      }
    }
  }

  private def combine(input: List[(Chunk, Seq[Span], Position)],
    output: ListBuffer[Block], parents: LinkedList[ListBuffer[Block]], top: ListBuffer[Block]): Seq[Block] = {
    if (input.isEmpty) return top

    input.head._1 match {
      case chunk @ CommandChunk(_, _, true, _) =>
        chunk.appendNewBlock(output, input.tail, input.head._2,
          input.head._3, this)
        val nest = output.last.asInstanceOf[Command].children
        parents.addLast(output)
        combine(input.tail, nest, parents, top)

      case chunk @ EndCommandChunk(_) =>
        log(chunk + ":" + parents)
        if (parents.size() == 0) {
          combine(input.tail, top, parents, top)
        } else {
          var parent = parents.pollLast()
          combine(input.tail, parent, parents, top)
        }

      case chunk @ _ =>
        chunk.appendNewBlock(output, input.tail, input.head._2,
          input.head._3, this)
        combine(input.tail, output, parents, top)
    }
  }

  def createSpanConverter2(linkDefinitions: Seq[LinkDefinitionChunk]): RakuzaSpanConverter =
    new RakuzaSpanConverter(linkDefinitions)

  override def knockoff(source: java.lang.CharSequence) = {

    val chunks = createChunkStream(new CharSequenceReader(source, 0))

    // These next lines are really ugly because I couldn't figure out a nice
    // way to match a tuple argument (thank you erasure!)
    val linkDefinitions = chunks.flatMap {
      case ((chunk, pos)) =>
        if (chunk.isLinkDefinition)
          List(chunk.asInstanceOf[LinkDefinitionChunk])
        else Nil
    }

    val convert = createSpanConverter2(linkDefinitions)

    val spanned = chunks.map { chunkAndPos =>
      (chunkAndPos._1, convert(chunkAndPos._1), chunkAndPos._2)
    }
    val list = new LinkedList[ListBuffer[Block]]
    val buffer = new ListBuffer[Block]
    combine(spanned.toList, buffer, list, buffer)
  }
  def chunk(source: java.lang.CharSequence) = {
    createChunkStream(new CharSequenceReader(source, 0))
  }

}