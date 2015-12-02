<?php

    include_once('db_connect.inc.php');

    //ensure all inputs have been sent
    if(!isset($_POST['id']) || !isset($_POST['email']) || !isset($_POST['name']) || !isset($_POST['e_id']) || !isset($_POST['type']))
    {
            $result["success"] = -99;
            $result["message"] = "Missing required POST parameters (email, id, name, e_id)";
            echo json_encode ( $result );
            exit();
    } 
    $email = $_POST['email'];
    $id = $_POST['id'];
    $name = $_POST['name'];
    $eid = $_POST['e_id'];
    $type = $_POST['type'];
    $response = array();
    $response["type"] = $type;
   
    if($type === 'delete')
    {
        $stmt = $mysqli->prepare("delete from items_tobring where id = ?");
        $stmt->bind_param('s', $id);
    }
    else if ($type === 'update')
    {
        if ($email === '')
        {
            $stmt = $mysqli->prepare("update items_tobring set email = NULL, name = ? where id = ?");
            $stmt->bind_param('ss', $name,$id);
        }
        else
        {
            $stmt = $mysqli->prepare("update items_tobring set email = ?, name = ? where id = ?");
            $stmt->bind_param('sss', $email, $name, $id);
        }
    }
    else if ($type === 'insert')
    {
        $stmt = $mysqli->prepare("insert into items_tobring (name, e_id) values (?, ?)");
        $stmt->bind_param('ss', $name, $eid);
    }

    
    $stmt->execute();
    $row_cnt = $stmt->num_rows;


    if($mysqli->affected_rows > 0)
    {
        $response["success"] = 1;
        $response["message"] = "Successfully updated item checklist!"; 
    }
    else
    {
        $response["success"] = 0;
        $response["message"] = "Failed to update item checklist!"; 
    }

    $stmt->close();
    echo(json_encode($response));
    $mysqli->close();
    
	

?>
