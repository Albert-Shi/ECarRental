<?php	//http://www.jb51.net/article/78478.htm
$file = $_FILES['file'];//�õ����������
//�õ��ļ�����
$name = $file['name'];
$type = strtolower(substr($name,strrpos($name,'.')+1)); //�õ��ļ����ͣ����Ҷ�ת����Сд
echo $type;
$allow_type = array('jpg','jpeg','gif','png'); //���������ϴ�������
//�ж��ļ������Ƿ������ϴ�
if(!in_array($type, $allow_type)){
  //�������������ֱ��ֹͣ��������
  return ;
}
//�ж��Ƿ���ͨ��HTTP POST�ϴ���
if(!is_uploaded_file($file['tmp_name'])){
  //�������ͨ��HTTP POST�ϴ���
  return ;
}
$upload_path = "./cars/"; //�ϴ��ļ��Ĵ��·��
//��ʼ�ƶ��ļ�����Ӧ���ļ���
if(move_uploaded_file($file['tmp_name'],$upload_path.$_GET['newname'].$file['name'])){
  echo "success";
}else{
  echo "failed";
}