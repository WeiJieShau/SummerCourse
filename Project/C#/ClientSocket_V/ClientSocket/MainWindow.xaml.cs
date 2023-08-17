using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Threading;
using Newtonsoft.Json;
using System.Net;
using System.Net.Sockets;
using System.Reflection;

namespace ClientSocket
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    /// class Client
    /// 

    public class DataFormat
    {
        public string Name { get; set; }
        public string Mes { get; set; }
    }

public partial class MainWindow : Window
    {
        static Socket clientSocket;
        public static TextBlock tblock_mess;
        DataFormat User = new DataFormat();
        private static bool isConnected = false;

        public void ConnectToServer(string ipAddress, int port)
        {
            try
            {
                IPAddress serverIp = IPAddress.Parse(ipAddress);

                clientSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
                clientSocket.Connect(new IPEndPoint(serverIp, port));


                // Update UI
                SetConnectionStatus(true);
                User.Mes = "Join";
                SendToServer(User);
                MainWindow.AppendMessage("Connected to server.");
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.ToString());
            }
        }
       
        public void DisconnectFromServer()
        {
            try
            {
                User.Mes = "Leave";
                SendToServer(User);
                Thread.Sleep(1000); // 暂停1秒



                clientSocket.Close();

                // Update UI
                SetConnectionStatus(false);
                MainWindow.AppendMessage("Disconnected from server.");
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.ToString);
            }
        }

        public static void SendToServer(object data)
        {
            try
            {
                string jsonData = JsonConvert.SerializeObject(data);
                byte[] sendData = Encoding.ASCII.GetBytes(jsonData);
                clientSocket.Send(sendData);
                
            }
            catch (Exception ex)
            {
               Console.WriteLine(ex.Message);
            }
        }

        public static void ReceiveMessages()
        {
            try
            {
                
                
                while (clientSocket.Connected)
                {
                    byte[] result = new byte[clientSocket.Available];
                    int receive_num = clientSocket.Receive(result);
                    if (receive_num > 0)
                    {
                        string received_str = Encoding.ASCII.GetString(result, 0, receive_num);
                        

                        // Parse the received JSON data
                        DataFormat receivedObject = JsonConvert.DeserializeObject<DataFormat>(received_str);
                        
                        // Display the received object's properties
                        AppendMessage( receivedObject.Name+ " : "+receivedObject.Mes);

                        if(receivedObject.Name=="Server" && receivedObject.Mes == "Leave, please Disconnect")
                        {
                            isConnected = false;
                            
                        }
                        

                    }


                    // Deserialize JSON data and update UI

                    
                }
                
            }
            catch (Exception ex)
            {
                Console.WriteLine($"{ex.Message}");
                
            }
        }
        

        
        public MainWindow()
        {
            InitializeComponent();
            tblock_mess = this.tblock_mes;
        }

        public static void AppendMessage(string message)
        {
            // Update UI to display the message
            Application.Current.Dispatcher.Invoke(() =>
            {
                tblock_mess.Text += message + Environment.NewLine;
            });
            // or use your preferred method to display the message
        }
        public void SetConnectionStatus(bool isConnected)
        {
            // Update UI to reflect connection status
            if (isConnected)
            {
                tblock_status.Background = Brushes.Green;
                tblock_status.Text = "Server is connected";


            }
            else
            {
                tblock_status.Background = Brushes.Red;
                tblock_status.Text = "Server is not connected";
            }

            // Other UI updates if needed
        }

        private void bt_con_Click_1(object sender, RoutedEventArgs e)
        {
            string ipAddress = tb_ip.Text;
            User.Name = tb_username.Text;
            int port = int.Parse(tb_port.Text);

            if (!isConnected)
            {
                // Connect to server
                ConnectToServer(ipAddress, port);
                bt_con.Content = "Disconnect";
                isConnected = true;

                // Start receiving messages in a separate thread
                Thread receiveThread = new Thread(ReceiveMessages);
                receiveThread.Start();
                
            }
            else
            {
                // Disconnect from server
                
                DisconnectFromServer();
                bt_con.Content = "Connect";
                isConnected = false;
            }
        }

        private void bt_send_Click_1(object sender, RoutedEventArgs e)
        {
            if (isConnected)
            {
                string message = tb_mes.Text;
                User.Mes = message;

                SendToServer(User);

            }
            else
            {
                AppendMessage("Not connected to the server.");
            }
        }

        private void tb_ip_TextChanged(object sender, TextChangedEventArgs e)
        {

        }

        private void tb_port_TextChanged(object sender, TextChangedEventArgs e)
        {

        }
    }
}

